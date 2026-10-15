package com.team.mazelab.character;


import com.team.mazelab.common.BaseTimeEntity;
import com.team.mazelab.member.Member;
import jakarta.persistence.*;
import org.hibernate.annotations.Check;

/**
 * 캐릭터. DB 테이블 "game_character" 한 줄.
 *
 * ★ ddl-auto: update 라서 "이 클래스가 곧 테이블 설계도"다.
 *   서버가 켜질 때 아래 어노테이션을 보고 create table 을 만든다.
 *   → 길이, NOT NULL, UNIQUE, CHECK 를 여기에 빠짐없이 적어야 DB 에도 생긴다.
 *
 * 만들어질 테이블 (서버 콘솔의 show-sql 로 확인)
 *   id         BIGINT PK AUTO_INCREMENT
 *   member_id  BIGINT NOT NULL UNIQUE → member(id) 외래키
 *   gold, ore, fish  INT NOT NULL (+ 음수 금지 CHECK)
 *   created_at, updated_at  DATETIME(6) NOT NULL  ← BaseTimeEntity 에서 물려받음
 *
 * 규칙
 * - 회원 1명 = 캐릭터 1개. 이름은 member.nickname 을 쓴다
 * - 재화는 setter 로 바꾸지 않는다. addGold / spendGold 같은 "이름 있는 메서드"로만 바꾼다
 *
 * EXTENSION (Step 5): 재화가 바뀔 때마다 activity_log 에 기록 (기록은 Service 가 남긴다)
 */


@Entity
@Table(name = "game_character")
// @Check : 테이블에 CHECK 제약을 만든다. 자바 검사를 뚫는 버그가 생겨도 DB 가 마지막으로 막는다
// ⚠ update 는 "테이블을 처음 만들 때만" 붙인다. 이미 있는 테이블에 나중에 추가하면 반영 안 됨
@Check(constraints = "gold >= 0 AND ore >= 0 AND fish >= 0")
public class GameCharacter extends  BaseTimeEntity{
    /** 처음 시작할 때 주는 골드. 상점의 D, C 등급 아이템을 한두 개 살 수 있는 정도 */
    public static final int START_GOLD = 300;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // AUTO_INCREMENT
    private Long id;


    // @OneToOne : 회원 하나에 캐릭터 하나
    // LAZY : 캐릭터를 꺼낼 때 회원은 "필요할 때" 따로 꺼낸다 (쓸데없는 SELECT 방지)
    // unique = true : member_id 에 UNIQUE → 한 회원이 캐릭터 2개를 못 만든다 (DB 가 보장)
    // @JoinColumn 이 있으면 update 가 member(id) 를 가리키는 외래키도 만들어 준다
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(nullable = false)
    private int gold;

    @Column(nullable = false)
    private int ore;

    @Column(nullable = false)
    private int fish;           // 낚시 잡화. 골드로 팔기만 가능

    protected GameCharacter() {}   // JPA 가 DB 값을 담을 때 쓰는 빈 생성자

    private GameCharacter(Member member, int gold) {
        this.member = member;
        this.gold = gold;
        this.ore = 0;
        this.fish = 0;
    }

    /** 새 캐릭터 만들기. 생성자 대신 이름 있는 정적 메서드 → 무슨 일을 하는지 이름으로 보인다 */
    public static GameCharacter createFor(Member member) {
        return new GameCharacter(member, START_GOLD);
    }

    // ===== 골드 =====
    public void addGold(int amount) {
        requirePositive(amount);
        this.gold += amount;
    }

    public void spendGold(int amount) {
        requirePositive(amount);
        if (gold < amount) {
            // 409 계열 : 요청은 맞지만 "지금 상태"로는 안 됨
            throw new IllegalStateException("골드가 부족합니다. (보유 " + gold + ", 필요 " + amount + ")");
        }
        this.gold -= amount;
    }

    // ===== 광석 =====
    public void addOre(int amount) {
        requirePositive(amount);
        this.ore += amount;
    }

    public void spendOre(int amount) {
        requirePositive(amount);
        if (ore < amount) {
            throw new IllegalStateException("광석이 부족합니다. (보유 " + ore + ", 필요 " + amount + ")");
        }
        this.ore -= amount;
    }

    // ===== 잡화 (낚시) =====
    public void addFish(int amount) {
        requirePositive(amount);
        this.fish += amount;
    }

    public void spendFish(int amount) {
        requirePositive(amount);
        if (fish < amount) {
            throw new IllegalStateException("잡화가 부족합니다. (보유 " + fish + ", 필요 " + amount + ")");
        }
        this.fish -= amount;
    }

    private static void requirePositive(int amount){
        if(amount <= 0){
            // 400 계열 : 요청 값 자체가 잘못됨
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다. (입력 " + amount + ")");
        }
    }


    // getter
    public Long getId() { return id; }
    public Member getMember() { return member; }
    public int getGold() { return gold; }
    public int getOre() { return ore; }
    public int getFish() { return fish; }

    /**
     * 캐릭터 이름 = 회원 닉네임.
     * ⚠ member 가 LAZY 라서 트랜잭션 "안"(Service)에서만 부를 수 있다 (open-in-view: false)
     *   그래서 JSON 으로 보낼 값은 Service 안에서 DTO(CharacterResponse)로 바꿔서 내보낸다
     */
    public String getNickname() { return member.getNickname(); }



}
