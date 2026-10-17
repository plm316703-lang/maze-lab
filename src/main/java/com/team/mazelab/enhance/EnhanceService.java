package com.team.mazelab.enhance;

import com.team.mazelab.activity.ActivityLogger;
import com.team.mazelab.activity.ActivityType;
import com.team.mazelab.activity.Delta;
import com.team.mazelab.character.CharacterResponse;
import com.team.mazelab.character.CharacterService;
import com.team.mazelab.character.GameCharacter;
import com.team.mazelab.common.ApiException;
import com.team.mazelab.inventory.InventoryItem;
import com.team.mazelab.inventory.InventoryRepository;
import com.team.mazelab.inventory.InventoryRowResponse;
import com.team.mazelab.item.Grade;
import com.team.mazelab.item.Item;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * 강화 규칙을 지키는 곳.
 *
 * @Transactional : 비용 차감, 재료 삭제, 등급 변경, 기록이 "한 묶음"
 *   중간에 예외가 나면 전부 취소된다 → "골드만 빠지고 강화는 안 된" 상태가 생기지 않는다
 */
@Service
@Transactional
public class EnhanceService {

    private final EnhanceRuleRepository ruleRepository;
    private final InventoryRepository inventoryRepository;
    private final CharacterService characterService;
    private final ActivityLogger activityLogger;
    private final EnhanceRoller roller;

    public EnhanceService(EnhanceRuleRepository ruleRepository,
                          InventoryRepository inventoryRepository,
                          CharacterService characterService,
                          ActivityLogger activityLogger,
                          EnhanceRoller roller) {
        this.ruleRepository = ruleRepository;
        this.inventoryRepository = inventoryRepository;
        this.characterService = characterService;
        this.activityLogger = activityLogger;
        this.roller = roller;
    }

    // ================= 강화표 조회 =================

    /**
     * GET /api/enhance-rules : D → C → B → A 순서로
     *
     * ★ DB 의 ORDER BY 를 쓰지 않고 자바에서 정렬하는 이유
     *   from_grade 는 "글자"로 저장돼 있어서 DB 로 정렬하면 A, B, C, D (알파벳 순) 가 된다.
     *   Comparator.comparing(getFromGrade) 는 enum 의 "선언 순서"(D, C, B, A, S, SS) 로 정렬한다
     */
    @Transactional(readOnly = true)
    public List<EnhanceRuleResponse> getRules() {
        return ruleRepository.findAll().stream()
            .sorted(Comparator.comparing(EnhanceRule::getFromGrade))
            .map(EnhanceRuleResponse::from)
            .toList();
    }

    // ================= 강화 =================

    public EnhanceResponse enhance(String username, Long inventoryId) {
        GameCharacter character = characterService.findByUsername(username);

        // ① 내 가방의 것인가 (남의 것이면 404)
        InventoryItem target = inventoryRepository.findByIdAndCharacter(inventoryId, character)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                "INVENTORY_NOT_FOUND", "가방에서 아이템을 찾을 수 없습니다. (번호 " + inventoryId + ")"));
        Item item = target.getItem();
        Grade before = target.getGrade();

        // ② 강화할 수 있는 장비인가
        if (!item.getSlot().isEquipment() || before == Grade.SS) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                "NOT_ENHANCEABLE", "재료와 SS 장비는 강화할 수 없습니다.");
        }
        if (target.isListed()) {
            throw new ApiException(HttpStatus.CONFLICT,
                "ITEM_LISTED", "시장에 올린 아이템은 강화할 수 없습니다.");
        }

        // ③ 규칙 찾기. S 는 규칙이 없다 = 마지막 등급
        EnhanceRule rule = ruleRepository.findById(before)
            .orElseThrow(() -> new ApiException(HttpStatus.CONFLICT,
                "MAX_GRADE", before + " 등급은 더 이상 강화할 수 없습니다."));

        // ④ 비용 검사 : 하나라도 부족하면 "아무것도 빼지 않고" 거절
        if (character.getGold() < rule.getGoldCost()) {
            throw new ApiException(HttpStatus.CONFLICT, "NOT_ENOUGH_GOLD",
                "골드가 부족합니다. (보유 " + character.getGold() + ", 필요 " + rule.getGoldCost() + ")");
        }
        if (character.getOre() < rule.getOreCost()) {
            throw new ApiException(HttpStatus.CONFLICT, "NOT_ENOUGH_ORE",
                "광석이 부족합니다. (보유 " + character.getOre() + ", 필요 " + rule.getOreCost() + ")");
        }

        // 희귀 재료 : 내 가방에서 그 아이템 번호이면서 시장에 안 올린 줄
        List<InventoryItem> materials = List.of();
        if (rule.needsMaterial()) {
            List<InventoryItem> owned = inventoryRepository
                .findByCharacterAndItem_IdAndListedFalseOrderByIdAsc(character, rule.getMaterialItemId());
            if (owned.size() < rule.getMaterialCount()) {
                throw new ApiException(HttpStatus.CONFLICT, "NOT_ENOUGH_MATERIAL",
                    "희귀 재료가 부족합니다. (보유 " + owned.size() + ", 필요 " + rule.getMaterialCount() + ")");
            }
            materials = owned.subList(0, rule.getMaterialCount());   // 오래된 것부터 필요한 개수만
        }

        // ⑤ 비용 차감 (검사를 모두 통과한 뒤에만!)
        //    spendGold(0) 은 requirePositive 에 걸리므로 0 보다 클 때만 부른다
        if (rule.getGoldCost() > 0) character.spendGold(rule.getGoldCost());
        if (rule.getOreCost() > 0) character.spendOre(rule.getOreCost());
        inventoryRepository.deleteAll(materials);                   // 재료 줄 DELETE

        // ⑥ 주사위 : 굴린 값이 성공률보다 작으면 성공
        boolean success = roller.roll() < rule.getSuccessRate();

        // ⑦ 결과 등급
        Grade after = success ? rule.getToGrade() : rule.gradeOnFail(before);
        target.changeGrade(after);

        // ⑧ 기록 : 골드, 광석은 빠졌으니 음수
        String detail = before + " → " + after + " / " + (success ? "성공" : "실패")
            + (materials.isEmpty() ? "" : " / 희귀 재료 " + materials.size() + "개 소모");
        activityLogger.log(character,
            success ? ActivityType.ENHANCE_SUCCESS : ActivityType.ENHANCE_FAIL,
            Delta.of(-rule.getGoldCost(), -rule.getOreCost(), 0),
            item, detail);

        // 응답 : 가방 전체를 다시 읽는다 (재료 삭제가 반영된 상태)
        List<InventoryRowResponse> inventory = inventoryRepository.findByCharacterOrderByIdAsc(character)
            .stream().map(InventoryRowResponse::from).toList();

        return new EnhanceResponse(success, before, after,
            InventoryRowResponse.from(target), inventory, CharacterResponse.from(character));
    }
}
