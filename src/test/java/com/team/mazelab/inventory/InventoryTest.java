package com.team.mazelab.inventory;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Step 3-3 검사 : 외형 키, 상점 구매, 장착/해제.
 *
 * 연습 계정 "user" 는 시작 골드 300 으로 시작한다 (H2 라서 매번 새로 만들어짐)
 * @Transactional : 테스트 하나가 끝나면 산 것, 쓴 골드가 전부 되돌아간다
 *
 * ★ POST 에는 .with(csrf()) 를 붙인다. 안 붙이면 시큐리티가 403 으로 막는다
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class InventoryTest {

    @Autowired MockMvc mvc;

    /** 도우미 : 상점 구매 요청 보내기 */
    private ResultActions buy(String username, long itemId) throws Exception {
        return mvc.perform(post("/api/shop/buy").with(user(username)).with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"itemId\":" + itemId + "}"));
    }

    /** 도우미 : 사고 나서 응답의 row.id (가방 번호) 꺼내기 */
    private long buyAndGetRowId(String username, long itemId) throws Exception {
        String json = buy(username, itemId).andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        // JsonPath.read : JSON 글자에서 "$.row.id" 위치의 값을 꺼낸다
        return ((Number) JsonPath.read(json, "$.row.id")).longValue();
    }

    @Test
    void 아이템_API에_외형키가_있다() throws Exception {
        mvc.perform(get("/api/items/1").with(user("user")))
            .andExpect(jsonPath("$.appearanceKey").value("weapon.sword"));   // 무기 : 자동
        mvc.perform(get("/api/items/8").with(user("user")))
            .andExpect(jsonPath("$.appearanceKey").value("armor.cloth"));    // 방어구 : 직접 적은 값
    }

    @Test
    void 상점에서_사면_골드가_빠지고_가방에_들어간다() throws Exception {
        buy("user", 1)   // 나무 검 50 골드
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.row.itemId").value(1))
            .andExpect(jsonPath("$.row.grade").value("D"))
            .andExpect(jsonPath("$.row.equippedSlot").doesNotExist())    // null
            .andExpect(jsonPath("$.character.gold").value(250))           // 300 - 50
            .andExpect(jsonPath("$.inventory").doesNotExist());           // 한 줄 변경 → row 만

        mvc.perform(get("/api/inventory").with(user("user")))
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void 상점에서_안파는_아이템은_거절() throws Exception {
        buy("user", 7)   // 용송곳니 검 = B 등급 → 상점에 없음
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("NOT_FOR_SALE"));
    }

    @Test
    void 골드가_부족하면_409_그리고_골드는_그대로() throws Exception {
        buy("user", 4).andExpect(status().isOk());   // 강철 대검 220 → 남은 골드 80
        buy("user", 23)                               // 기사의 장창 260 → 부족
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("NOT_ENOUGH_GOLD"));

        mvc.perform(get("/api/characters/me").with(user("user")))
            .andExpect(jsonPath("$.gold").value(80));  // 실패한 구매로는 한 푼도 안 빠짐
    }

    @Test
    void 같은칸에_장착하면_기존장비는_자동으로_빠진다() throws Exception {
        long sword = buyAndGetRowId("user", 1);   // 나무 검
        long bow = buyAndGetRowId("user", 2);     // 사냥꾼의 활 (둘 다 WEAPON 칸)

        mvc.perform(post("/api/inventory/" + sword + "/equip").with(user("user")).with(csrf()))
            .andExpect(status().isOk());

        // 활로 교체 → 응답은 가방 전체. [0]=검(해제됨), [1]=활(장착)
        mvc.perform(post("/api/inventory/" + bow + "/equip").with(user("user")).with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.inventory[0].equippedSlot").doesNotExist())
            .andExpect(jsonPath("$.inventory[1].equippedSlot").value("WEAPON"))
            .andExpect(jsonPath("$.character.gold").value(170));   // 300 - 50 - 80
    }

    @Test
    void 해제하면_가방으로_돌아간다() throws Exception {
        long sword = buyAndGetRowId("user", 1);
        mvc.perform(post("/api/inventory/" + sword + "/equip").with(user("user")).with(csrf()));

        mvc.perform(post("/api/inventory/" + sword + "/unequip").with(user("user")).with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.inventory[0].equippedSlot").doesNotExist());

        // 한 번 더 해제하면 409
        mvc.perform(post("/api/inventory/" + sword + "/unequip").with(user("user")).with(csrf()))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("NOT_EQUIPPED"));
    }

    @Test
    void 남의_가방_아이템은_장착할_수_없다() throws Exception {
        long mine = buyAndGetRowId("user", 1);    // user 가 산 검

        // admin 이 user 의 가방 번호로 장착 시도 → 404 (있는지조차 알려주지 않음)
        mvc.perform(post("/api/inventory/" + mine + "/equip").with(user("admin")).with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("INVENTORY_NOT_FOUND"));
    }

    @Test
    void itemId가_없으면_400() throws Exception {
        mvc.perform(post("/api/shop/buy").with(user("user")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }
}
