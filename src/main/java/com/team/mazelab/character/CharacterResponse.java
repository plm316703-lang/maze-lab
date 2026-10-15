package com.team.mazelab.character;


/**
 * GET /api/characters/me 의 응답 모양 (= 프론트와의 약속).
 *
 * record : 값만 담는 불변 상자. 칸 이름이 그대로 JSON 키가 된다
 *   → {"nickname":"민혁","gold":300,"ore":0,"fish":0}
 *
 * 왜 Entity 를 그대로 보내지 않나?
 *   1) Entity 에는 member(비밀번호 포함!) 같은 보내면 안 되는 값이 있다
 *   2) LAZY 칸을 JSON 으로 바꾸다가 에러가 난다
 *   3) DB 칸이 바뀌어도 이 모양만 지키면 프론트 코드는 안 바뀐다
 */
public record CharacterResponse(String nickname, int gold, int ore, int fish) {

    /** Entity → 응답 상자. 반드시 트랜잭션 안(Service)에서 부른다 (getNickname 이 LAZY) */
    public static CharacterResponse from(GameCharacter c){
        return new CharacterResponse(c.getNickname(), c.getGold(), c.getOre(), c.getFish());
    }
    // EXTENSION: 장착 장비 3칸, 공격력 같은 능력치가 생기면 칸을 "추가"한다
    //            (기존 칸 이름은 바꾸지 않는다 = 프론트와의 약속)
}
