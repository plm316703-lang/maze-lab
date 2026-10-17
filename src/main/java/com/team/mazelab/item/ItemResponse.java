package com.team.mazelab.item;


/**
 * 아이템 한 줄의 응답 모양.
 *   {"id":1,"name":"나무 검","slot":"WEAPON","weaponType":"SWORD","tier":"D",
 *    "appearanceKey":"weapon.sword","price":50,"atk":3,"def":0,"hp":0}
 *
 * ★ Step 3-3 : appearanceKey 추가 (프론트 연동 명세의 약속)
 *   record 는 칸 순서 = 생성자 매개변수 순서. 칸을 끼워 넣었으면 from() 의 순서도 같이 맞춘다
 */
public record ItemResponse(Long id, String name, ItemSlot slot, WeaponType weaponType,
                           Grade tier, String appearanceKey ,int price, int atk, int def, int hp) {

    public static ItemResponse from(Item it) {
        return new ItemResponse(it.getId(), it.getName(), it.getSlot(), it.getWeaponType(),
            it.getTier(), it.getAppearanceKey() ,it.getPrice(), it.getAtk(), it.getDef(), it.getHp());
    }
}
