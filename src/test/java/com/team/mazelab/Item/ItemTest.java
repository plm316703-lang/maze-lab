package com.team.mazelab.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Step 3-1 검사 : 카탈로그, 상점 목록, 에러 JSON, 401
 */
@SpringBootTest
@AutoConfigureMockMvc
class ItemTest {

    @Autowired ItemRepository itemRepository;
    @Autowired MockMvc mvc;

    @Test
    void 서버가_켜지면_카탈로그_28개가_들어간다() {
        assertThat(itemRepository.count()).isEqualTo(ItemCatalogInitializer.CATALOG.size());
        assertThat(itemRepository.count()).isEqualTo(28);
    }

    @Test
    void 상점은_DC_장비만_판다() throws Exception {
        mvc.perform(get("/api/items/shop").with(user("user")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(16)))                 // D·C 장비 16개
            .andExpect(jsonPath("$[?(@.tier == 'B')]").isEmpty())  // B 없음
            .andExpect(jsonPath("$[?(@.slot == 'MATERIAL')]").isEmpty());
    }

    @Test
    void 재료는_등급이_없다() throws Exception {
        mvc.perform(get("/api/items/5001").with(user("user")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("빛나는 원석"))
            .andExpect(jsonPath("$.tier").doesNotExist());         // null
    }

    @Test
    void 없는_아이템은_404_JSON() throws Exception {
        mvc.perform(get("/api/items/9999").with(user("user")))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("ITEM_NOT_FOUND"));
    }

    @Test
    void 로그인_안하면_API는_401_JSON() throws Exception {
        mvc.perform(get("/api/items"))                            // .with(user) 없음
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }
}
