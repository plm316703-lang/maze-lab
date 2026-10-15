package com.team.mazelab.member;

import com.team.mazelab.character.CharacterService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 연습용 회원 심기. 서버가 켜질 때 한 번 실행된다 (CommandLineRunner).
 * user / admin, 비밀번호 1234.
 *
 * ★ 닉네임, 이메일에 UNIQUE 가 있으니 두 계정의 값은 서로 달라야 한다.
 *  ★ Step 2 : 연습 계정에도 캐릭터가 있어야 게임 기능을 테스트할 수 있다.
 */
@Component
public class MemberDataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CharacterService characterService;   // ★ Step 2 추가

    public MemberDataInitializer(MemberRepository memberRepository,
                                 PasswordEncoder passwordEncoder,
                                 CharacterService characterService) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.characterService = characterService;
    }

    @Override
    public void run(String... args) {
        // orElseGet : 이미 있으면 꺼내 쓰고, 없으면 만들어서 저장
        //   → 서버를 여러 번 켜도 회원이 중복으로 들어가지 않는다

        Member user = memberRepository.findByUsername("user").orElseGet(() ->
            memberRepository.save(new Member(
                "user",
                passwordEncoder.encode("1234"),
                "일반회원",
                "user@test.com",
                Role.USER)));

        Member admin = memberRepository.findByUsername("admin").orElseGet(() ->
            memberRepository.save(new Member(
                "admin",
                passwordEncoder.encode("1234"),
                "관리자",
                "admin@test.com",
                Role.ADMIN)));
        // Step 1 때 이미 만들어진 연습 계정은 캐릭터가 없다 → "없으면 만들기"
        characterService.ensureCharacter(user);
        characterService.ensureCharacter(admin);
    }
}
