package com.team.mazelab.member;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate; // [필수] LocalDate 임포트 추가

/**
 * [3번 회원 영토] 연습용 회원 심기.
 * 서버 켜질 때 DB member 테이블에 user / admin 을 넣는다.
 * 1번(미로), 2번(데이터)이 "회원 객체"가 필요할 때 이 두 명을 쓴다.
 * 비밀번호는 1234.
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
                LocalDate.of(2000, 1, 1), // 테스트용 생년월일
                "M",                      // 테스트용 성별
                Role.USER
            ));
        }

        // 관리자 초기 데이터 (생년월일과 성별 추가)
        if (!memberRepository.existsByUsername("admin")) {
            memberRepository.save(new Member(
                "admin",
                passwordEncoder.encode("1234"),
                "관리자",
                LocalDate.of(1995, 5, 5), // 테스트용 생년월일
                "F",                      // 테스트용 성별
                Role.ADMIN
            ));
        }
    }
}
