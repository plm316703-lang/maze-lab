package com.team.mazelab.member;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 연습용 회원 심기. 서버가 켜질 때 한 번 실행된다 (CommandLineRunner).
 * user / admin, 비밀번호 1234.
 *
 * ★ 닉네임, 이메일에 UNIQUE 가 있으니 두 계정의 값은 서로 달라야 한다.
 * ★ 이 파일을 안 고치면 Member 생성자가 바뀌어서 컴파일 에러 → 서버가 아예 안 켜진다.
 */
@Component
public class MemberDataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberDataInitializer(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 일반회원 초기 데이터 (생년월일과 성별 추가)
        if (!memberRepository.existsByUsername("user")) {
            memberRepository.save(new Member(
                "user",
                passwordEncoder.encode("1234"),
                "일반회원",
                "user@test.com", // 테스트용 생년월일
                Role.USER
            ));
        }

        // 관리자 초기 데이터 (생년월일과 성별 추가)
        if (!memberRepository.existsByUsername("admin")) {
            memberRepository.save(new Member(
                "admin",
                passwordEncoder.encode("1234"),
                "관리자",
                "admin@test.com",
                Role.ADMIN
            ));
        }
        // EXTENSION (Step 2): 연습 계정에도 캐릭터가 있어야 게임 기능을 테스트할 수 있다
        //                     → 저장 직후 characterService.createFor(...) 를 부른다
    }
}
