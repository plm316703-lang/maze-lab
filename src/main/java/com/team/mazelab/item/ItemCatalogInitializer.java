package com.team.mazelab.item;


import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.team.mazelab.item.Grade.*;
import static com.team.mazelab.item.ItemSlot.*;
import static com.team.mazelab.item.WeaponType.*;

@Component
public class ItemCatalogInitializer implements CommandLineRunner  {

    private final ItemRepository itemRepository;

    public ItemCatalogInitializer(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /** 카탈로그 원본. 테스트에서도 개수 확인에 쓴다 */
    static final List<Item> CATALOG = List.of(
        // ===== 무기 11개 : weapon(번호, 이름, 종류, 등급, 가격, 공격) =====
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

        // ===== 방어구 7개 : gear(번호, 이름, 칸, 등급, 가격, 공격, 방어, 체력) =====
        Item.gear(8,  "천 옷",         ARMOR, D, 40,  0, 2,  0),
        Item.gear(9,  "가죽 갑옷",     ARMOR, D, 90,  0, 4,  0),
        Item.gear(10, "마법 로브",     ARMOR, C, 150, 0, 3,  15),
        Item.gear(11, "숲의 망토",     ARMOR, C, 180, 0, 5,  10),
        Item.gear(12, "사슬 갑옷",     ARMOR, C, 210, 0, 7,  10),
        Item.gear(13, "강철 판금",     ARMOR, B, 420, 0, 10, 20),
        Item.gear(14, "수호자의 갑주", ARMOR, B, 720, 0, 14, 40),

        // ===== 장신구 6개 =====
        Item.gear(15, "구리 반지",          ACCESSORY, D, 60,  1, 1, 0),
        Item.gear(16, "생명의 부적",        ACCESSORY, C, 150, 0, 0, 25),
        Item.gear(17, "광부의 목걸이",      ACCESSORY, C, 200, 0, 0, 0),   // EXTENSION: 광산 +50%
        Item.gear(18, "맑은 정신의 귀걸이", ACCESSORY, B, 250, 0, 0, 0),   // EXTENSION: 전투 기력 +1
        Item.gear(19, "사냥꾼의 휘장",      ACCESSORY, B, 300, 0, 0, 0),   // EXTENSION: 전투 골드 +30%
        Item.gear(20, "호박 속 초파리",     ACCESSORY, B, 520, 0, 0, 0),   // EXTENSION: 전조 항상 보기

        // ===== SS 2개 (보스 전용, 강화 불가) =====
        Item.gear(101, "심연룡의 갑주",   ARMOR,     SS, 2000, 0,  30, 100),
        Item.gear(102, "심연룡의 눈동자", ACCESSORY, SS, 2000, 18, 9,  0),

        // ===== 재료 2개 (강화 재료) =====
        Item.material(5001, "빛나는 원석"),   // 광산 희귀 드롭, B → A 강화
        Item.material(5002, "심해 진주")      // 낚시 희귀 드롭, A → S 강화
    );

    @Override
    @Transactional
    public void run(String... args){
        for(Item item : CATALOG){
            if(!itemRepository.existsById(item.getId())){
                itemRepository.save(item);
            }
        }
    }
}
