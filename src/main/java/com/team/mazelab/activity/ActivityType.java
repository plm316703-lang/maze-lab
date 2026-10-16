package com.team.mazelab.activity;

/**
 * 활동 종류. 프로토타입 v23 의 log(...) 에 쓰인 종류와 같은 이름을 쓴다 (프론트와의 약속).
 * label : 화면에 보일 한글 이름
 *
 * DB 에는 글자(EnumType.STRING)로 저장 → 새 종류는 여기에 한 줄 추가하면 끝 (테이블 변경 없음)
 */

public enum ActivityType {
    CHARACTER_CREATED("원정대 합류"),

    // 상점, 장비 (Step 3-3)
    SHOP_BUY("상점 구매"),
    EQUIP("장착"),
    UNEQUIP("장착 해제"),

    // 강화 (Step 4)
    ENHANCE_SUCCESS("강화 성공"),
    ENHANCE_FAIL("강화 실패"),

    // 광산, 낚시, 판매 (Step 5)
    MINE_COLLECT("광석 수령"),
    MINE_RARE("광산 희귀 재료"),
    MINE_LEVEL("광산 강화"),
    FISH_COLLECT("잡화 수령"),
    FISH_RARE("낚시 희귀 재료"),
    RESOURCE_SELL("재화 판매"),

    // 자유시장 (Step 6)
    TRADE_LIST("시장 등록"),
    TRADE_CANCEL("시장 등록 취소"),
    TRADE_BUY("시장 구매"),
    TRADE_SOLD("시장 판매 완료"),

    // 사냥, 보스 (Step 7)
    QUICK_HUNT("빠른 사냥"),
    HUNT_DROP("사냥 전리품"),
    BOSS_WIN("보스 격파");

    private final String label;

    ActivityType(String label){
        this.label = label;
    }

    public String label(){ return label; }
}
