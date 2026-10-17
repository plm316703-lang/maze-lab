package com.team.mazelab.item;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.team.mazelab.item.Grade.*;
import static com.team.mazelab.item.ItemSlot.*;
import static com.team.mazelab.item.WeaponType.*;

/**
 * 서버가 켜질 때 아이템 카탈로그(28개)를 DB 에 맞춘다.
 *
 * ★ Step 3-3 변경 : "없으면 넣기" → "없으면 넣고, 있으면 빈 칸 채우기"
 *   appearance_key 칸이 새로 생기면 기존 28줄은 NULL 이다.
 *   그래서 이미 있는 줄도 꺼내서 syncFrom() 으로 값을 채운다.
 */
@Component
public class ItemCatalogInitializer implements CommandLineRunner {

    private final ItemRepository itemRepository;

    public ItemCatalogInitializer(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /** 카탈로그 원본. 무기는 외형 키가 자동, 나머지는 맨 끝에 직접 적는다 */
    static final List<Item> CATALOG = List.of(
        // ===== 무기 11개 : weapon(번호, 이름, 종류, 등급, 가격, 공격) — 변경 없음 =====
        Item.weapon(1,  "나무 검",       SWORD, D, 50,  3),
        Item.weapon(2,  "사냥꾼의 활",   BOW,   D, 80,  4),
        Item.weapon(3,  "견습 지팡이",   STAFF, D, 80,  4),
        Item.weapon(21, "훈련용 창",     SPEAR, D, 80,  4),
        Item.weapon(22, "벌목 도끼",     AXE,   D, 90,  5),
        Item.weapon(4,  "강철 대검",     SWORD, C, 220, 8),
        Item.weapon(5,  "은빛 장궁",     BOW,   C, 220, 8),
        Item.weapon(23, "기사의 장창",   SPEAR, C, 260, 9),
        Item.weapon(6,  "별빛 지팡이",   STAFF, B, 300, 10),
        Item.weapon(24, "광전사의 도끼", AXE,   B, 400, 12),
        Item.weapon(7,  "용송곳니 검",   SWORD, B, 650, 15),

        // ===== 방어구 7개 : gear(번호, 이름, 칸, 등급, 가격, 공격, 방어, 체력, 외형키) =====
        Item.gear(8,  "천 옷",         ARMOR, D, 40,  0, 2,  0,  "armor.cloth"),
        Item.gear(9,  "가죽 갑옷",     ARMOR, D, 90,  0, 4,  0,  "armor.leather"),
        Item.gear(10, "마법 로브",     ARMOR, C, 150, 0, 3,  15, "armor.mage_robes"),
        Item.gear(11, "숲의 망토",     ARMOR, C, 180, 0, 5,  10, "armor.forest_cloak"),
        Item.gear(12, "사슬 갑옷",     ARMOR, C, 210, 0, 7,  10, "armor.chainmail"),
        Item.gear(13, "강철 판금",     ARMOR, B, 420, 0, 10, 20, "armor.steel_plate"),
        Item.gear(14, "수호자의 갑주", ARMOR, B, 720, 0, 14, 40, "armor.guardian_plate"),

        // ===== 장신구 6개 =====
        Item.gear(15, "구리 반지",          ACCESSORY, D, 60,  1, 1, 0,  "accessory.copper_ring"),
        Item.gear(16, "생명의 부적",        ACCESSORY, C, 150, 0, 0, 25, "accessory.life_talisman"),
        Item.gear(17, "광부의 목걸이",      ACCESSORY, C, 200, 0, 0, 0,  "accessory.miner_necklace"),      // EXTENSION: 광산 +50%
        Item.gear(18, "맑은 정신의 귀걸이", ACCESSORY, B, 250, 0, 0, 0,  "accessory.clear_mind_earrings"), // EXTENSION: 전투 기력 +1
        Item.gear(19, "사냥꾼의 휘장",      ACCESSORY, B, 300, 0, 0, 0,  "accessory.hunter_badge"),        // EXTENSION: 전투 골드 +30%
        Item.gear(20, "호박 속 초파리",     ACCESSORY, B, 520, 0, 0, 0,  "accessory.amber_fly"),           // EXTENSION: 전조 항상 보기

        // ===== SS 2개 (보스 전용, 강화 불가) =====
        Item.gear(101, "심연룡의 갑주",   ARMOR,     SS, 2000, 0,  30, 100, "armor.abyss_dragon"),
        Item.gear(102, "심연룡의 눈동자", ACCESSORY, SS, 2000, 18, 9,  0,   "accessory.abyss_eye"),

        // ===== 재료 2개 : material(번호, 이름, 외형키) =====
        Item.material(5001, "빛나는 원석", "material.shining_gem"),   // 광산 희귀 드롭, B → A 강화
        Item.material(5002, "심해 진주",   "material.deep_pearl")     // 낚시 희귀 드롭, A → S 강화
    );

    @Override
    @Transactional   // 이 안에서 바꾼 값은 끝날 때 한 번에 UPDATE 된다
    public void run(String... args) {
        for (Item source : CATALOG) {
            // ifPresentOrElse(있을 때, 없을 때)
            itemRepository.findById(source.getId()).ifPresentOrElse(
                saved -> saved.syncFrom(source),     // 있으면 : 빈 칸 채우기 (UPDATE)
                () -> itemRepository.save(source)    // 없으면 : 새로 넣기 (INSERT)
            );
        }
    }
}
