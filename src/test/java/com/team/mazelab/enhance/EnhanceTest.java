package com.team.mazelab.enhance;

import com.team.mazelab.character.GameCharacter;
import com.team.mazelab.character.GameCharacterRepository;
import com.team.mazelab.inventory.InventoryItem;
import com.team.mazelab.inventory.InventoryRepository;
import com.team.mazelab.item.Grade;
import com.team.mazelab.item.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Step 4 검사 : 강화표, 성공/실패, 비용, 희귀 재료, A→S 하락.
 *
 * ★ @MockitoBean EnhanceRoller roller
 *   진짜 주사위 대신 "가짜 주사위" 를 끼운다.
 *   given(roller.roll()).willReturn(0.0)  → 무조건 성공 (0.0 은 어떤 확률보다 작다)
 *   given(roller.roll()).willReturn(0.99) → 무조건 실패
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EnhanceTest {

    @Autowired MockMvc mvc;
    @Autowired GameCharacterRepository characterRepository;
    @Autowired InventoryRepository inventoryRepository;
    @Autowired ItemRepository itemRepository;

    @MockitoBean EnhanceRoller roller;

    GameCharacter me;

    /** 각 테스트 전에 : 연습 계정 "user" 에게 넉넉한 재화 지급 (300 + 5000 골드, 광석 500) */
    @BeforeEach
    void setUp() {
        me = characterRepository.findByMember_Username("user").orElseThrow();
        me.addGold(5000);
        me.addOre(500);
    }

    /** 도우미 : 가방에 아이템을 원하는 등급으로 직접 넣고 가방 번호 돌려주기 */
    private long give(long itemId, Grade grade) {
        InventoryItem row = InventoryItem.acquire(me, itemRepository.findById(itemId).orElseThrow(), grade);
        return inventoryRepository.save(row).getId();
    }

    private ResultActions enhance(long inventoryId) throws Exception {
        return mvc.perform(post("/api/inventory/" + inventoryId + "/enhance").with(user("user")).with(csrf()));
    }

    @Test
    void 강화표는_D부터_A까지_4줄() throws Exception {
        mvc.perform(get("/api/enhance-rules").with(user("user")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(4))
            .andExpect(jsonPath("$[0].fromGrade").value("D"))    // 알파벳 순(A)이 아니라 등급 순
            .andExpect(jsonPath("$[3].fromGrade").value("A"))
            .andExpect(jsonPath("$[3].failGrade").value("B"));
    }

    @Test
    void D_강화_성공하면_C가_되고_비용이_빠진다() throws Exception {
        given(roller.roll()).willReturn(0.0);
        long sword = give(1, Grade.D);

        enhance(sword)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.before").value("D"))
            .andExpect(jsonPath("$.after").value("C"))
            .andExpect(jsonPath("$.row.grade").value("C"))
            .andExpect(jsonPath("$.character.gold").value(5250))   // 5300 - 50
            .andExpect(jsonPath("$.character.ore").value(495));    // 500 - 5
    }

    @Test
    void 실패하면_등급은_그대로_비용만_빠진다() throws Exception {
        given(roller.roll()).willReturn(0.99);
        long sword = give(1, Grade.C);

        enhance(sword)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.after").value("C"))
            .andExpect(jsonPath("$.character.gold").value(5150));  // 5300 - 150
    }

    @Test
    void B에서_A는_원석이_없으면_409_그리고_아무것도_안_빠진다() throws Exception {
        long sword = give(1, Grade.B);

        enhance(sword)
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("NOT_ENOUGH_MATERIAL"));

        mvc.perform(get("/api/characters/me").with(user("user")))
            .andExpect(jsonPath("$.gold").value(5300));
    }

    @Test
    void B에서_A_성공하면_원석이_가방에서_사라진다() throws Exception {
        given(roller.roll()).willReturn(0.0);
        long sword = give(1, Grade.B);
        give(5001, null);                         // 빛나는 원석 (재료는 등급 없음)

        enhance(sword)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.after").value("A"))
            .andExpect(jsonPath("$.inventory.length()").value(1));   // 검만 남음
    }

    @Test
    void A에서_S_실패하면_B로_떨어진다() throws Exception {
        given(roller.roll()).willReturn(0.99);
        long sword = give(1, Grade.A);
        give(5002, null);                         // 심해 진주

        enhance(sword)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.after").value("B"))
            .andExpect(jsonPath("$.inventory.length()").value(1));   // 진주는 실패해도 소모
    }

    @Test
    void SS_장비는_강화할_수_없다() throws Exception {
        long armor = give(101, Grade.SS);         // 심연룡의 갑주

        enhance(armor)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("NOT_ENHANCEABLE"));
    }

    @Test
    void S는_마지막_등급() throws Exception {
        long sword = give(1, Grade.S);

        enhance(sword)
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("MAX_GRADE"));
    }
}
