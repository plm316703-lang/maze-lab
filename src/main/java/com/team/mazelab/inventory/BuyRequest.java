package com.team.mazelab.inventory;

import jakarta.validation.constraints.NotNull;

/**
 * POST /api/shop/buy 의 요청 본문.  {"itemId": 1}
 *
 * ★ 받는 건 itemId 하나뿐. 가격, 등급, 캐릭터 번호는 받지 않는다
 *   → 가격은 서버의 도감(item.price)에서, 캐릭터는 로그인 정보에서 꺼낸다
 *   (프론트가 "price": 1 을 보내서 싸게 사는 조작이 불가능)
 *
 * @NotNull : itemId 가 빠지면 Controller 의 @Valid 가 막는다 → 400
 */
public record BuyRequest(@NotNull Long itemId) {
}
