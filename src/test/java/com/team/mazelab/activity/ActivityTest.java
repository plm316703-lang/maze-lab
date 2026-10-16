package com.team.mazelab.activity;

import com.team.mazelab.character.GameCharacter;
import com.team.mazelab.character.GameCharacterRepository;
import com.team.mazelab.member.MemberFormDto;
import com.team.mazelab.member.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Step 3-2 검사 : 기록이 남는지, API 로 보이는지, 트랜잭션 밖에서는 막히는지
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional   // 테스트가 끝나면 DB 변경을 되돌린다
class ActivityTest {

    @Autowired MemberService memberService;
    @Autowired GameCharacterRepository characterRepository;
    @Autowired ActivityLogRepository activityLogRepository;
    @Autowired ActivityLogger activityLogger;
    @Autowired MockMvc mvc;

    @Test
    void 캐릭터가_생기면_합류_기록이_남는다() {
        MemberFormDto dto = new MemberFormDto();
        dto.setUsername("logtester");
        dto.setPassword("password123");
        dto.setPasswordConfirm("password123");
        dto.setNickname("기록원");
        dto.setEmail("log@test.com");

        memberService.saveMember(dto);

        GameCharacter c = characterRepository.findByMember_Username("logtester").orElseThrow();
        List<ActivityLog> logs = activityLogRepository.findTop100ByCharacterOrderByIdDesc(c);

        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getActivityType()).isEqualTo(ActivityType.CHARACTER_CREATED);
        assertThat(logs.get(0).getGoldDelta()).isEqualTo(GameCharacter.START_GOLD);
        assertThat(logs.get(0).getCreatedAt()).isNotNull();   // @CreatedDate 가 채워졌는지
    }

    @Test
    void 내_활동기록_API는_최신순_JSON() throws Exception {
        // 연습 계정 user 는 서버가 켜질 때 캐릭터가 만들어지면서 합류 기록이 1줄 생긴다
        mvc.perform(get("/api/activity").with(user("user")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].type").value("CHARACTER_CREATED"))
            .andExpect(jsonPath("$[0].typeLabel").value("원정대 합류"))
            .andExpect(jsonPath("$[0].goldDelta").value(GameCharacter.START_GOLD))
            .andExpect(jsonPath("$[0].itemId").doesNotExist());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)   // 이 테스트만 트랜잭션 "없이" 실행
    void 트랜잭션_밖에서는_기록할_수_없다() {
        GameCharacter c = characterRepository.findByMember_Username("user").orElseThrow();

        assertThatThrownBy(() -> activityLogger.log(c, ActivityType.EQUIP, Delta.NONE, "테스트"))
            .isInstanceOf(IllegalTransactionStateException.class);   // MANDATORY 가 막았다
    }
}
