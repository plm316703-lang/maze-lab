package com.team.mazelab.item;

import jakarta.persistence.*;

/**
 * 아이템 "종류" (카탈로그). DB 테이블 "item".
 * ※ 내가 가진 아이템 하나하나는 inventory 테이블 (Step 3-3). 여기는 "도감"이다.
 *
 * ★ id 를 DB 가 매기지 않고 "우리가 정한다" (@GeneratedValue 없음)
 *   아이템 번호는 프론트와의 약속이라 서버를 새로 깔아도 항상 같은 번호여야 하기 때문이다.
 *   (1 = 나무 검, 101 = 심연룡의 갑주, 5001 = 빛나는 원석 ...)
 *
 * tier : 이 아이템이 "처음 나올 때"의 등급. 강화한 지금 등급은 inventory.grade 에 따로 저장한다.
 *        재료(MATERIAL)는 등급이 없어서 NULL.
 */
@Entity
@Table(name = "item")
public class Item {

    @Id
    private Long id;                       // 우리가 정하는 번호

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)           // "WEAPON" 글자로 저장
    @Column(nullable = false, length = 20)
    private ItemSlot slot;

    @Enumerated(EnumType.STRING)
    @Column(name = "weapon_type", length = 20)   // 무기만 값이 있고 나머지는 NULL
    private WeaponType weaponType;

    @Enumerated(EnumType.STRING)
    @Column(length = 5)                    // 재료는 NULL
    private Grade tier;

    @Column(nullable = false)
    private int price;                     // 상점 가격 (되팔 때 기준)

    @Column(nullable = false)
    private int atk;

    @Column(nullable = false)
    private int def;

    @Column(nullable = false)
    private int hp;

    // EXTENSION: 장신구 특수효과 (광산 +50%, 전조 보기 ...) 는 effect 칸을 "추가"해서 넣는다

    protected Item() {}   // JPA 용

    private Item(Long id, String name, ItemSlot slot, WeaponType weaponType,
                 Grade tier, int price, int atk, int def, int hp) {
        this.id = id;
        this.name = name;
        this.slot = slot;
        this.weaponType = weaponType;
        this.tier = tier;
        this.price = price;
        this.atk = atk;
        this.def = def;
        this.hp = hp;
    }

    // ===== 만드는 방법 3가지 : 이름만 보고 무엇을 만드는지 알 수 있게 =====

    /** 무기 */
    public static Item weapon(long id, String name, WeaponType type, Grade tier, int price, int atk) {
        return new Item(id, name, ItemSlot.WEAPON, type, tier, price, atk, 0, 0);
    }

    /** 방어구 또는 장신구 */
    public static Item gear(long id, String name, ItemSlot slot, Grade tier, int price, int atk, int def, int hp) {
        if (slot == ItemSlot.WEAPON || slot == ItemSlot.MATERIAL) {
            throw new IllegalArgumentException("gear() 는 ARMOR, ACCESSORY 만 만듭니다.");
        }
        return new Item(id, name, slot, null, tier, price, atk, def, hp);
    }

    /** 재료 (등급, 능력치, 가격 없음) */
    public static Item material(long id, String name) {
        return new Item(id, name, ItemSlot.MATERIAL, null, null, 0, 0, 0, 0);
    }

    // getter
    public Long getId() { return id; }
    public String getName() { return name; }
    public ItemSlot getSlot() { return slot; }
    public WeaponType getWeaponType() { return weaponType; }
    public Grade getTier() { return tier; }
    public int getPrice() { return price; }
    public int getAtk() { return atk; }
    public int getDef() { return def; }
    public int getHp() { return hp; }

    /** 상점에서 파는 물건인가 : 장비이면서 처음 등급이 D 또는 C */
    public boolean isSoldInShop() {
        return slot.isEquipment() && tier != null && tier.isShopGrade();
    }
}
