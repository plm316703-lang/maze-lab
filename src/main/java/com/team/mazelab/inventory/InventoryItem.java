package com.team.mazelab.inventory;


import com.team.mazelab.character.GameCharacter;
import com.team.mazelab.common.BaseTimeEntity;
import com.team.mazelab.item.Grade;
import com.team.mazelab.item.Item;
import com.team.mazelab.item.ItemSlot;
import jakarta.persistence.*;

/**
 * 가방 속 아이템 "한 개". DB 테이블 "inventory".
 *
 *   item(도감)      : "나무 검이라는 아이템이 있다"  (모두가 같은 28줄)
 *   inventory(가방) : "민혁이가 나무 검 1개를 C 등급으로 갖고 있다" (사람마다 다름)
 *
 * 만들어질 테이블
 *   id             BIGINT PK AUTO_INCREMENT
 *   character_id   BIGINT NOT NULL → game_character(id)   누구 가방인가
 *   item_id        BIGINT NOT NULL → item(id)             무슨 아이템인가
 *   grade          VARCHAR(5) NOT NULL                    지금 등급 (강화하면 바뀜)
 *   equipped_slot  VARCHAR(20) NULL                       장착 중이면 WEAPON/ARMOR/ACCESSORY, 아니면 NULL
 *   listed         BIT NOT NULL                           자유시장에 올려 둔 상태인가 (Step 6)
 *   created_at, updated_at
 *
 * ★ UNIQUE (character_id, equipped_slot)
 *   "한 캐릭터는 한 칸에 하나만 장착" 을 DB 가 보장한다.
 *   NULL 은 UNIQUE 검사에서 서로 다른 값으로 취급된다 → 장착 안 한 아이템은 몇 개든 OK
 *
 * 규칙 : 한 줄 = 아이템 1개. 재료(원석, 진주)도 1개씩 한 줄 (수량 칸 없음, 프론트와의 약속)
 */

@Entity
@Table(name = "inventory",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_inventory_equipped",
        columnNames = {"character_id", "equipped_slot"}),
    indexes = @Index(name = "idx_inventory_character_id", columnList = "cgaracter_id, id"))
 public class InventoryItem extends BaseTimeEntity{

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@ManyToOne : 캐릭터 1명이 가방 줄 여러 개를 가진다( 다 : 1)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "character_id", nullable = false)
    private GameCharacter character;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 5)
    private Grade grade;

    @Enumerated(EnumType.STRING)
    @Column(name = "equipped_slot", length = 20)
    private ItemSlot equippedSlot;      // null = 가방에만 있음

    @Column(nullable = false)
    private boolean listed;

    protected InventoryItem() {}

    private InventoryItem(GameCharacter character, Item item, Grade grade) {
        this.character = character;
        this.item = item;
        this.grade = grade;
        this.equippedSlot = null;
        this.listed = false;
    }


    /**
     * 가방에 새 아이템 넣기. 등급을 따로 받는 이유:
     *   상점 구매 → 도감 등급(tier) 그대로,  사냥 드롭(Step 7) → 다른 등급이 나올 수 있음
     */
    public static InventoryItem acquire(GameCharacter character, Item item, Grade grade) {
        return new InventoryItem(character, item, grade);
    }


    // ===== 장착 / 해제 : Entity 는 "자기 상태 규칙"만 지킨다 =====

    /** 장착. 칸은 아이템 종류가 정한다 (무기는 WEAPON 칸) */
    public void equip() {
        if (!item.getSlot().isEquipment()) {
            throw new IllegalArgumentException("재료는 장착할 수 없습니다.");
        }
        if (listed) {
            throw new IllegalStateException("시장에 올린 아이템은 장착할 수 없습니다.");
        }
        this.equippedSlot = item.getSlot();
    }

    /** 해제. 가방으로 돌아간다 */
    public void unequip() {
        this.equippedSlot = null;
    }

    public boolean isEquipped() {
        return equippedSlot != null;
    }

    // EXTENSION (Step 4): public void changeGrade(Grade next) — 강화 성공/실패로 등급 변경
    // EXTENSION (Step 6): public void list() / unlist() — 시장 등록/취소 (장착 중이면 거부)

    // getter
    public Long getId() { return id; }
    public GameCharacter getCharacter() { return character; }
    public Item getItem() { return item; }
    public Grade getGrade() { return grade; }
    public ItemSlot getEquippedSlot() { return equippedSlot; }
    public boolean isListed() { return listed; }
}



