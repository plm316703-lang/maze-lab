package com.team.mazelab.activity;

import com.team.mazelab.character.GameCharacter;
import com.team.mazelab.item.Item;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 활동 기록 한 줄. DB 테이블 "activity_log".
 *
 * - 한 번 쓰면 고치지 않는다 (setter 도, 수정 메서드도 없음) → 그래서 updated_at 이 없다
 *   BaseTimeEntity(created_at + updated_at) 대신 @CreatedDate 칸 하나만 직접 둔다
 * - 2번 역할의 핵심 데이터 : 나중에 "오늘 광산으로 번 광석" = SUM(ore_delta) WHERE activity_type='MINE_COLLECT'
 *
 * ★ 인덱스 : "내 최근 기록" 은 character_id 로 거르고 id 역순으로 정렬한다.
 *   (character_id, id) 인덱스가 있으면 기록이 수만 줄이 되어도 빠르게 찾는다.
 *   (update 는 테이블을 "처음 만들 때" 인덱스도 같이 만든다)
 */

@Entity
@Table(name = "activity_log",
    indexes = @Index(name = "idx_activity_character_id", columnList = "character_id, id"))
@EntityListeners(AuditingEntityListener.class) // @CreatedDate 를 채우는 장치 (JpaConfig 의 @EnableJpaAuditing 과 짝)
public class ActivityLog {

    //detail 칸 최대 길이
    private static final int DETAIL_MAX = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 여러 기록 → 캐릭터 하나 (@ManyToOne). LAZY : 기록을 꺼낼 때 캐릭터는 필요할 때만
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "character_id", nullable = false)
    private GameCharacter character;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false, length = 30)
    private ActivityType activityType;

    @Column(name = "gold_delta", nullable = false)
    private int goldDelta;

    @Column(name = "ore_delta", nullable = false)
    private int oreDelta;

    @Column(name = "fish_delta", nullable = false)
    private int fishDelta;

    // 관련 아이템 (없으면 NULL). 강화한 장비, 얻은 재료, 거래한 장비 등
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(length = DETAIL_MAX)
    private String detail;                 // 사람이 읽는 설명 ("B → A / 성공")

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected ActivityLog() {}   // JPA 용

    private ActivityLog(GameCharacter character, ActivityType type, Delta delta, Item item, String detail) {
        this.character = character;
        this.activityType = type;
        this.goldDelta = delta.gold();
        this.oreDelta = delta.ore();
        this.fishDelta = delta.fish();
        this.item = item;
        // 너무 긴 설명은 잘라서 저장 (DB 칸보다 길면 저장 자체가 실패하므로)
        this.detail = (detail != null && detail.length() > DETAIL_MAX) ? detail.substring(0, DETAIL_MAX) : detail;
    }

    /** 기록 한 줄 만들기. item 은 없으면 null */
    public static ActivityLog of(GameCharacter character, ActivityType type, Delta delta, Item item, String detail) {
        return new ActivityLog(character, type, delta, item, detail);
    }

    // getter (읽기만)
    public Long getId() { return id; }
    public GameCharacter getCharacter() { return character; }
    public ActivityType getActivityType() { return activityType; }
    public int getGoldDelta() { return goldDelta; }
    public int getOreDelta() { return oreDelta; }
    public int getFishDelta() { return fishDelta; }
    public Item getItem() { return item; }
    public String getDetail() { return detail; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
