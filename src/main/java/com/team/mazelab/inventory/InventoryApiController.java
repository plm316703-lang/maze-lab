package com.team.mazelab.inventory;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * 가방 + 상점 JSON API.
 *
 *   GET  /api/inventory                 내 가방
 *   POST /api/shop/buy   {"itemId":1}   상점 구매
 *   POST /api/inventory/{id}/equip      장착
 *   POST /api/inventory/{id}/unequip    해제
 *
 * 주소가 /api/inventory 와 /api/shop 두 갈래라서 클래스에는 공통 앞머리 "/api" 만 둔다.
 * Controller 는 "받아서 넘기기" 만 한다. 규칙은 전부 Service 에.
 *
 * ★ POST 는 CSRF 토큰이 필요하다 (SecurityConfig 기본값).
 *   프론트는 <meta name="_csrf"> 값을 헤더에 실어서 보낸다
 */

import java.util.List;
@RestController
@RequestMapping("/api")
public class InventoryApiController {

    private final InventoryService inventoryService;

    public InventoryApiController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/inventory")
    public List<InventoryRowResponse> myInventory(@AuthenticationPrincipal UserDetails user) {
        return inventoryService.getMyInventory(user.getUsername());
    }

    /**
     * @RequestBody : 요청 본문 JSON {"itemId":1} → BuyRequest 로 바꿔 준다
     * @Valid       : BuyRequest 의 @NotNull 검사를 실행한다. 실패하면 이 메서드에 들어오지도 않는다
     */
    @PostMapping("/shop/buy")
    public InventoryActionResponse buy(@AuthenticationPrincipal UserDetails user,
                                       @Valid @RequestBody BuyRequest request) {
        return inventoryService.buy(user.getUsername(), request.itemId());
    }

    /** @PathVariable : 주소의 {id} 칸 값을 꺼낸다. /api/inventory/500/equip → id = 500 */
    @PostMapping("/inventory/{id}/equip")
    public InventoryActionResponse equip(@AuthenticationPrincipal UserDetails user,
                                         @PathVariable("id") Long id) {
        return inventoryService.equip(user.getUsername(), id);
    }

    @PostMapping("/inventory/{id}/unequip")
    public InventoryActionResponse unequip(@AuthenticationPrincipal UserDetails user,
                                           @PathVariable("id") Long id) {
        return inventoryService.unequip(user.getUsername(), id);
    }
}
