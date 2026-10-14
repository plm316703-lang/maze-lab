package com.team.mazelab;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * [공용] 기초 땅이 멀쩡한지 검사.
 * 누가 공용 파일을 잘못 고치면 여기서 빨간불 → PR 올리기 전에 꼭 돌린다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class BaseTest {

    @Autowired
    MockMvc mvc;

    @Test
    void 첫화면은_누구나_본다() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    void 연습계정으로_로그인된다() throws Exception {
        mvc.perform(formLogin("/login").user("user").password("1234"))
           .andExpect(authenticated());
    }

    @Test
    void 로그인_안하면_막힌곳에서_쫓겨난다() throws Exception {
        mvc.perform(get("/somewhere"))
           .andExpect(status().is3xxRedirection());
    }
}
