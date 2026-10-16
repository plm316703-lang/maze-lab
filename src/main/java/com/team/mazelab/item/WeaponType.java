package com.team.mazelab.item;


/**
 * 무기 종류 = 공격 방식. 직업 대신 무기가 사거리를 정한다.
 * range : 3줄 전장에서 같은 줄로 몇 칸까지 닿는지 (프로토타입 v23 WEAPON_TYPES 값)
 *
 * EXTENSION (Step 7~8): 스킬 이름, 줄 무시(지팡이) 같은 전투 규칙도 여기에 모은다
 */
public enum WeaponType {
    SWORD("검", 1),
    BOW("활", 3),
    SPEAR("창", 2),
    STAFF("지팡이", 2),
    AXE("도끼", 1);

    private final String label;
    private final int range;

    WeaponType(String label, int range) {
        this.label = label;
        this.range = range;
    }

    public String label() { return label; }
    public int range() { return range; }
}
