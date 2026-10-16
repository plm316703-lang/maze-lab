package com.team.mazelab.item;


/**
 * 아이템 한 줄의 응답 모양.
 *   {"id":1,"name":"나무 검","slot":"WEAPON","weaponType":"SWORD","tier":"D","price":50,"atk":3,"def":0,"hp":0}
 *
 * enum 은 JSON 에서 글자("WEAPON")로 나간다. null 은 null 로 나간다 (재료의 tier 등)
 */
public record ItemResponse(Long id, String name, ItemSlot slot, WeaponType weaponType,
                           Grade tier, int price, int atk, int def, int hp) {

    public static ItemResponse from(Item it) {
        return new ItemResponse(it.getId(), it.getName(), it.getSlot(), it.getWeaponType(),
            it.getTier(), it.getPrice(), it.getAtk(), it.getDef(), it.getHp());
    }
}
