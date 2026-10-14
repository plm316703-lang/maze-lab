package com.team.mazelab.member;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 1 & 2 & 3. 회원가입 처리 (비밀번호 암호화, 생년월일, 성별, 권한 지정)
    public Member saveMember(MemberFormDto memberFormDto) {
        // 아이디 중복 검사
        validateDuplicateMember(memberFormDto.getUsername());

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(memberFormDto.getPassword());

        // 역할(Role) 기본값 설정 (넘어온 값이 없으면 USER로 지정)
        Role role = memberFormDto.getRole() != null ? memberFormDto.getRole() : Role.USER;

        // 생년월일과 성별이 포함된 Member 객체 생성
        Member member = new Member(
            memberFormDto.getUsername(),
            encodedPassword,
            memberFormDto.getName(),
            memberFormDto.getBirthDate(), // 생년월일 추가
            memberFormDto.getGender(),     // 성별 추가
            role
        );

        Member saved = memberRepository.save(member);

        // TODO [2번 약속] RegionProgressService 가 생기면 아래 한 줄을 켠다 (docs/DATA_CONTRACT.md B-1)
        //      같은 트랜잭션 안이라, 이 줄이 실패하면 회원가입도 함께 취소된다.
        // regionProgressService.initFor(saved);

        return saved;
    }

    private void validateDuplicateMember(String username) {
        memberRepository.findByUsername(username).ifPresent(member -> {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        });
    }

    // 4. 회원 탈퇴
    public void withdrawMember(String username) {
        memberRepository.findByUsername(username).ifPresent(member -> {
            memberRepository.delete(member);
        });
    }
}
