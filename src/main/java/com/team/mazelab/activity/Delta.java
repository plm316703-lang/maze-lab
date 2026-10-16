package com.team.mazelab.activity;

/**
 * 재화 증감 묶음. "골드 −50, 광석 −5" 처럼 한 번의 활동에서 여러 재화가 함께 움직일 수 있다.
 *
 * ★ GameCharacter.addGold() 와 달리 음수를 허용한다.
 *   기록은 "얼마나 늘고 줄었나"를 적는 것이라 −50(썼음)도 정상 값이다.
 *
 * 사용 예:
 *   Delta.gold(300)                  → 골드 +300
 *   Delta.of(-50, -5, 0)             → 골드 −50, 광석 −5 (강화 비용)
 *   Delta.NONE                       → 재화 변화 없음 (장착 등)
 */

public record Delta(int gold, int ore, int fish) {
  public static final Delta NONE = new Delta(0,0, 0);

  public static Delta gold(int amount){ return new Delta(amount, 0, 0);}
    public static Delta ore(int amount)  { return new Delta(0, amount, 0); }
    public static Delta fish(int amount) { return new Delta(0, 0, amount); }

    public static  Delta of(int gole, int ore, int fish){return new Delta(gole, ore, fish);}

}
