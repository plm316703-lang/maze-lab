package com.team.mazelab.inventory;


import com.team.mazelab.activity.ActivityLogger;
import com.team.mazelab.activity.ActivityType;
import com.team.mazelab.activity.Delta;
import com.team.mazelab.character.CharacterResponse;
import com.team.mazelab.character.CharacterService;
import com.team.mazelab.character.GameCharacter;
import com.team.mazelab.common.ApiException;
import com.team.mazelab.item.Item;
import com.team.mazelab.item.ItemRepository;
import com.team.mazelab.item.ItemSlot;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 가방 규칙을 지키는 곳 : 상점 구매, 장착, 해제.
 *
 * 모든 메서드의 공통 흐름
 *   1) username 으로 내 캐릭터 찾기 (요청에서 캐릭터 번호를 받지 않는다)
 *   2) 규칙 검사 → 어기면 ApiException (프론트가 code 로 구분)
 *   3) 값 바꾸기 → 활동 기록 남기기
 *   4) 트랜잭션 "안"에서 DTO 로 바꿔서 돌려주기 (LAZY 때문)
 *
 * @Transactional : 한 메서드 = 한 묶음. 중간에 예외가 나면 골드 차감, 가방 추가, 기록이 "전부" 취소된다
 */

@Service
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;
    private final CharacterService characterService;
    private final ActivityLogger activityLogger;

    public InventoryService(InventoryRepository inventoryRepository,
                            ItemRepository itemRepository,
                            CharacterService characterService,
                            ActivityLogger activityLogger) {
        this.inventoryRepository = inventoryRepository;
        this.itemRepository = itemRepository;
        this.characterService = characterService;
        this.activityLogger = activityLogger;
    }
    // ================= 조회 =================
    ///** GET /api/inventory : 내 가방 전체 */
    @Transactional(readOnly = true)
    public List<InventoryRowResponse> getMyInventory(String username){
        GameCharacter character = characterService.findByUsername(username);
        return toRows(character);
    }

    // ================= 상점 구매 =================
    /**
     * POST /api/shop/buy
     *   도감 확인 → 상점 판매품인지 → 골드 충분한지 → 골드 차감 → 가방에 추가 → 기록
     * 응답 : 한 줄 추가 → row + character
     */
    public InventoryActionResponse buy(String username, Long itemId){
        GameCharacter character = characterService.findByUsername(username);

        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                "ITEM_NOT_FOUND", "아이템을 찾을 수 없습니다.(번호 " + itemId + ")"));

        // 상점은 D, C 장비만 판다. B 이상, SS, 재료를 사려고 하면 거절
        if(!item.isSoldInShop()){
            throw new ApiException(HttpStatus.BAD_REQUEST,
                "NOT_FOR_SALE", "상점에서 팔지 않는 아이템입니다. (" + item.getName() + ")");
        }
        // 골드 검사를 여기서 "먼저" 한다 → 프론트가 구분할 수 있는 code(NOT_ENOUGH_GOLD)를 주려고.
        // spendGold() 안에도 검사가 있지만 그건 "마지막 안전장치" (거기서 터지면 일반 CONFLICT)
        if (character.getGold() < item.getPrice()) {
            throw new ApiException(HttpStatus.CONFLICT,
                "NOT_ENOUGH_GOLD", "골드가 부족합니다. (보유 " + character.getGold()
                + ", 필요 " + item.getPrice() + ")");
        }

        character.spendGold(item.getPrice());   // UPDATE game_character (트랜잭션 끝날 때)

        // 상점 물건은 도감 등급(tier) 그대로 들어온다
        InventoryItem row = inventoryRepository.save(
            InventoryItem.acquire(character, item, item.getTier()));

        // 기록 : 골드는 "빠졌으니" 음수
        activityLogger.log(character, ActivityType.SHOP_BUY,
            Delta.gold(-item.getPrice()), item, item.getName() + " 구매");

        return InventoryActionResponse.ofRow(
            InventoryRowResponse.from(row), CharacterResponse.from(character));
    }

    // ================= 장착 =================

    /**
     * POST /api/inventory/{id}/equip
     *   같은 칸에 이미 장착한 게 있으면 "자동으로 빼고" 새 것을 낀다 (교체)
     * 응답 : 2줄이 바뀔 수 있음 → inventory 전체 + character
     */
    public InventoryActionResponse equip(String username, Long inventoryId){
        GameCharacter character = characterService.findByUsername(username);
        InventoryItem target = findMind(character, inventoryId);
        ItemSlot slot = target.getItem().getSlot();

        // Service 에서 먼저 검사 → 프론트용 code 를 준다 (Entity 의 검사는 마지막 안전장치)
        if (!slot.isEquipment()) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                "NOT_EQUIPPABLE", "재료는 장착할 수 없습니다.");
        }
        if (target.isListed()) {
            throw new ApiException(HttpStatus.CONFLICT,
                "ITEM_LISTED", "시장에 올린 아이템은 장착할 수 없습니다.");
        }
        // 이미 끼고 있는 걸 또 누르면 아무것도 안 바꾸고 현재 상태만 돌려준다 (같은 요청 두 번 = 같은 결과)
        if (!target.isEquipped()) {
            // 1) 그 칸에 끼고 있던 기존 장비를 뺀다
            inventoryRepository.findByCharacterAndEquippedSlot(character, slot)
                .ifPresent(old -> {
                    old.unequip();
                    activityLogger.log(character, ActivityType.UNEQUIP, Delta.NONE,
                        old.getItem(), "교체로 해제");
                });

            // 2) ★ flush : 지금까지 바꾼 내용(기존 장비 해제)을 DB 에 "먼저" 보낸다
            //    안 하면 트랜잭션 끝에 UPDATE 2개가 한꺼번에 나가는데, 순서가 보장되지 않는다.
            //    "새 장비 장착" UPDATE 가 먼저 나가면 그 순간 WEAPON 칸이 2개
            //    → UNIQUE(character_id, equipped_slot) 위반 에러!
            inventoryRepository.flush();
            // 3) 새 장비를 낀다
            target.equip();
            activityLogger.log(character, ActivityType.EQUIP, Delta.NONE,
                target.getItem(), target.getItem().getName() + " 장착");
        }

        return InventoryActionResponse.ofInventory(toRows(character), CharacterResponse.from(character));
    }

    // ================= 해제 =================

    /**
     * POST /api/inventory/{id}/unequip
     * 응답 : 장착 칸 표시가 바뀌므로 inventory 전체 + character (프론트 권장 방식)
     */
    public InventoryActionResponse unequip(String username, Long inventoryId) {
        GameCharacter character = characterService.findByUsername(username);
        InventoryItem target = findMind(character, inventoryId);

        if (!target.isEquipped()) {
            throw new ApiException(HttpStatus.CONFLICT,
                "NOT_EQUIPPED", "장착 중인 아이템이 아닙니다.");
        }

        target.unequip();
        activityLogger.log(character, ActivityType.UNEQUIP, Delta.NONE,
            target.getItem(), target.getItem().getName() + " 해제");

        return InventoryActionResponse.ofInventory(toRows(character), CharacterResponse.from(character));
    }

    // ================= 내부 도우미 =================

    /**
     * 내 가방의 한 줄 찾기. 남의 것이거나 없는 번호면 똑같이 404.
     *   "남의 것이라 403" 이라고 알려 주면 → "그 번호가 존재한다" 는 정보가 새어 나간다
     */
    private InventoryItem findMind(GameCharacter character, Long inventoryId){
        return inventoryRepository.findByIdAndCharacter(inventoryId, character)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                "INVENTORY_NOT_FOUND", "가방에서 아이템을 찾을 수 없습니다. (번호 " + inventoryId + ")"));
    }
    /** 가방 전체 → 응답 줄 목록 */
    private List<InventoryRowResponse> toRows(GameCharacter character) {
        return inventoryRepository.findByCharacterOrderByIdAsc(character).stream()
            .map(InventoryRowResponse::from)
            .toList();
    }
    // EXTENSION (Step 4): enhance(username, inventoryId) — 강화. 재료 줄 삭제 + 등급 변경 → inventory 전체 응답
    // EXTENSION (Step 6): 시장 등록/구매 시 소유자 변경

}
