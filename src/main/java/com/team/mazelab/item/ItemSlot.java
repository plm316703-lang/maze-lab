package com.team.mazelab.item;

/**
 * 아이템이 들어가는 칸.
 * WEAPON, ARMOR, ACCESSORY 는 장착 칸 3개와 같다. MATERIAL(재료)은 장착할 수 없다.
 */
public enum  ItemSlot {
    WEAPON, ARMOR, ACCESSORY, MATERIAL;

    public boolean isEquipment(){
        return this != MATERIAL;
    }

}
