package com.team.mazelab.enhance;


import com.team.mazelab.item.Grade;
import jakarta.persistence.*;
import org.hibernate.annotations.Check;

/**
 * 강화 규칙 한 줄. DB 테이블 "enhance_rule".
 *   "D 등급을 강화하면 → 90% 확률로 C, 광석 5 + 골드 50 소모"
 *
 * ★ PK 가 숫자 id 가 아니라 from_grade (현재 등급) 이다
 *   등급 하나당 규칙은 딱 하나 → "D 의 규칙" 을 찾을 때 findById(Grade.D) 로 바로 찾는다
 *   S, SS 는 줄이 없다 = 강화할 수 없다 (규칙이 없으면 거절)
 *
 * 만들어질 테이블
 *   from_grade       VARCHAR(5) PK
 *   to_grade         VARCHAR(5) NOT NULL
 *   success_rate     DOUBLE NOT NULL   (0.0 ~ 1.0, CHECK)
 *   ore_cost, gold_cost  INT NOT NULL
 *   material_item_id BIGINT NULL       필요한 희귀 재료 (없으면 NULL)
 *   material_count   INT NOT NULL
 *   fail_grade       VARCHAR(5) NULL   실패 시 떨어질 등급 (NULL = 그대로)
 *
 * ★ material_item_id 를 Item 과 @ManyToOne 으로 잇지 않은 이유
 *   서버가 켜질 때 "아이템 도감" 과 "강화 규칙" 을 채우는 순서가 정해져 있지 않다.
 *   외래키를 걸면 규칙이 먼저 들어갈 때 "없는 아이템을 가리킨다" 에러가 날 수 있다.
 *   → 번호만 저장하고, 쓰는 쪽(EnhanceService)에서 그 번호로 가방을 찾는다
 */

@Entity
@Table(name = "enhance_rule")
@Check(constraints = "success_rate >= 0 AND success_rate <= 1")
public class EnhanceRule {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "from_grade", length = 5)
    private Grade fromGrade;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_grade", nullable = false, length = 5)
    private Grade toGrade;

    @Column(name = "success_rate", nullable = false)
    private double successRate;           // 0.9 = 90%

    @Column(name = "ore_cost", nullable = false)
    private int oreCost;

    @Column(name = "gold_cost", nullable = false)
    private int goldCost;

    @Column(name = "material_item_id")
    private Long materialItemId;          // 5001 빛나는 원석, 5002 심해 진주, 없으면 null

    @Column(name = "material_count", nullable = false)
    private int materialCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "fail_grade", length = 5)
    private Grade failGrade;              // A→S 만 B, 나머지는 null

    protected EnhanceRule() {}

    private EnhanceRule(Grade fromGrade, Grade toGrade, double successRate, int oreCost, int goldCost,
                        Long materialItemId, int materialCount, Grade failGrade) {
        this.fromGrade = fromGrade;
        this.toGrade = toGrade;
        this.successRate = successRate;
        this.oreCost = oreCost;
        this.goldCost = goldCost;
        this.materialItemId = materialItemId;
        this.materialCount = materialCount;
        this.failGrade = failGrade;
    }

    /** 희귀 재료가 필요 없는 규칙 (D→C, C→B) */
    public static EnhanceRule basic(Grade from, Grade to, double rate, int ore, int gold) {
        return new EnhanceRule(from, to, rate, ore, gold, null, 0, null);
    }

    /** 희귀 재료가 필요한 규칙 (B→A, A→S). failGrade 가 null 이면 실패해도 등급 유지 */
    public static EnhanceRule withMaterial(Grade from, Grade to, double rate, int ore, int gold,
                                           long materialItemId, int materialCount, Grade failGrade) {
        return new EnhanceRule(from, to, rate, ore, gold, materialItemId, materialCount, failGrade);
    }

    public void syncFrom(EnhanceRule source) {
        this.toGrade = source.toGrade;
        this.successRate = source.successRate;
        this.oreCost = source.oreCost;
        this.goldCost = source.goldCost;
        this.materialItemId = source.materialItemId;
        this.materialCount = source.materialCount;
        this.failGrade = source.failGrade;
    }

    /** 희귀 재료가 필요한 규칙인가 */
    public boolean needsMaterial() {
        return materialItemId != null && materialCount > 0;
    }

    /** 실패했을 때의 등급 : 떨어질 등급이 정해져 있으면 그것, 아니면 지금 등급 그대로 */
    public Grade gradeOnFail(Grade current) {
        return failGrade != null ? failGrade : current;
    }

    // getter
    public Grade getFromGrade() { return fromGrade; }
    public Grade getToGrade() { return toGrade; }
    public double getSuccessRate() { return successRate; }
    public int getOreCost() { return oreCost; }
    public int getGoldCost() { return goldCost; }
    public Long getMaterialItemId() { return materialItemId; }
    public int getMaterialCount() { return materialCount; }
    public Grade getFailGrade() { return failGrade; }


}
