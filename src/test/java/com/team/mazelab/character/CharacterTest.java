package com.team.mazelab.character;

import com.team.mazelab.member.MemberFormDto;
import com.team.mazelab.member.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Step 2 검사.
 * @Transactional : 테스트가 끝나면 DB 변경을 되돌린다 → 테스트끼리 서로 영향 없음
 * 테스트는 src/test/resources/application.yml 의 H2 메모리 DB 에서 돈다 → 진짜 DB 는 안전
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CharacterTest {

    @Autowired MemberService memberService;
    @Autowired GameCharacterRepository characterRepository;
    @Autowired MockMvc mvc;

    @Test
    void 회원가입하면_캐릭터가_자동으로_생긴다() {
        MemberFormDto dto = new MemberFormDto();
        dto.setUsername("newbie");
        dto.setPassword("password123");
        dto.setPasswordConfirm("password123");
        dto.setNickname("새내기");
        dto.setEmail("newbie@test.com");

        memberService.saveMember(dto);

        GameCharacter c = characterRepository.findByMember_Username("newbie").orElseThrow();
        assertThat(c.getGold()).isEqualTo(GameCharacter.START_GOLD);
        assertThat(c.getOre()).isZero();
        assertThat(c.getNickname()).isEqualTo("새내기");
    }

    @Test
    void 가진_골드보다_많이_쓰면_실패하고_골드는_그대로() {
        GameCharacter c = characterRepository.findByMember_Username("user").orElseThrow();
        int before = c.getGold();

        assertThatThrownBy(() -> c.spendGold(before + 1))
            .isInstanceOf(IllegalStateException.class);
        assertThat(c.getGold()).isEqualTo(before);   // 실패했으면 한 푼도 안 빠져야 한다
    }

    @Test
    void 음수_수량은_거부() {
        GameCharacter c = characterRepository.findByMember_Username("user").orElseThrow();
        assertThatThrownBy(() -> c.addGold(-5))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 내_캐릭터_API는_JSON으로_돌려준다() throws Exception {
        mvc.perform(get("/api/characters/me").with(user("user")))   // user("user") : 로그인한 척
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nickname").value("일반회원"))
            .andExpect(jsonPath("$.gold").value(GameCharacter.START_GOLD))
            .andExpect(jsonPath("$.password").doesNotExist());       // 비밀번호가 새어 나가지 않는지
    }
}
