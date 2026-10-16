package com.team.mazelab.item;


/**
 * 장비 등급. D < C < B < A < S < SS
 *
 * - 강화로 오를 수 있는 끝은 S. SS 는 보스 전용이고 강화 불가 (규칙은 enhance_rule 테이블, Step 4)
 * - multiplier : 실제 능력치 = 기본 능력치 × 배율 (프로토타입 v23 값, 연동 명세 결정 3번)
 *
 * ★ DB 에는 @Enumerated(EnumType.STRING) 으로 "글자"로 저장한다.
 *   순서 번호(ordinal)로 저장하면, 나중에 등급 사이에 새 등급을 끼워 넣을 때 기존 데이터의 뜻이 바뀐다.
 */
public enum Grade {
    D(1.0), C(1.25), B(1.65), A(2.2), S(3.0), SS(1.0);
    // SS 가 1.0 인 이유 : SS 아이템은 기본 능력치 자체가 이미 높게 잡혀 있다 (프로토타입 기준)

    private final double multiplier;

    Grade(double multiplier){
        this.multiplier = multiplier;
    }
    public double multiplier() { return multiplier; }
    /** 상점에서 파는 등급인가 (D, C) */
    public boolean isShopGrade() {
        return this == D || this == C;
    }

    /** 사냥으로 얻을 수 있는 등급인가 (D ~ B) */
    public boolean isHuntDropGrade() {
        return this == D || this == C || this == B;
    }

    /** 능력치 계산 : 기본값 × 배율, 반올림 */
    public int apply(int baseStat) {
        return (int) Math.round(baseStat * multiplier);
    }

}
