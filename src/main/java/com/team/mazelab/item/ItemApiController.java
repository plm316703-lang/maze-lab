package com.team.mazelab.item;
import com.team.mazelab.common.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * 아이템 카탈로그 API (읽기 전용).
 * 프론트는 게임 시작 때 한 번 불러서, 이름과 능력치를 이 값으로 보여준다.
 *
 * (읽기만 하는 단순 조회라 Service 없이 Repository 를 바로 쓴다.
 *  규칙이 생기는 구매/장착은 Service 를 거친다 → Step 3-3)
 */

@RestController
@RequestMapping("/api/items")
@Transactional(readOnly = true)
public class ItemApiController {

    private final ItemRepository itemRepository;

    public ItemApiController(ItemRepository  itemRepository ){
        this.itemRepository = itemRepository;
    }

    /** GET /api/items : 전체 카탈로그 (28개) */
    @GetMapping
    public List<ItemResponse> all(){
        return itemRepository.findAllByOrderByIdAsc().stream()
            .map(ItemResponse::from)    //item -> itemResponse로 하나씩 바꾸기
            .toList();
    }

    /** GET /api/items/shop : 상점 판매 목록 (D·C 장비) */
    @GetMapping("/shop")
    public List<ItemResponse> shop() {
        return itemRepository.findBySlotNotAndTierInOrderByIdAsc(ItemSlot.MATERIAL, Set.of(Grade.D, Grade.C))
            .stream().map(ItemResponse::from).toList();
    }

    /** GET /api/items/{id} : 하나. 없으면 404 + {"code":"ITEM_NOT_FOUND"} */
    @GetMapping("/{id}")
    public ItemResponse one(@PathVariable("id") Long id) {
        return itemRepository.findById(id)
            .map(ItemResponse::from)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                "ITEM_NOT_FOUND", "아이템을 찾을 수 없습니다. (번호 " + id + ")"));
    }
}
