package com.team.mazelab.member;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 회원 규칙을 지키는 곳.
 * 규칙을 어기면 IllegalStateException 을 던진다
 * → MemberController 가 잡아서 회원가입 화면에 메시지로 보여준다.
 */
@Service
@Transactional   // 이 클래스의 메서드는 전부 "한 묶음": 중간에 실패하면 DB 변경이 모두 취소된다
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;  //비밀번호를 암호화

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** 회원가입 : 중복 검사 → 비밀번호 암호화 → 저장 */
    public Member saveMember(MemberFormDto dto) {
        // 이메일은 앞뒤 공백을 지우고 소문자로 통일 → "ABC@x.com" 과 "abc@x.com" 을 같은 이메일로 본다
        String email = dto.getEmail().trim().toLowerCase();
        validateDuplicate(dto.getUsername(), dto.getNickname(), email);
        String encodedPassword = passwordEncoder.encode(dto.getPassword());


        // 권한은 서버가 정한다. 회원가입으로는 항상 USER
        Member member = new Member(dto.getUsername(), encodedPassword, dto.getNickname(), email, Role.USER);
        Member saved = memberRepository.save(member);
// EXTENSION (Step 2): 캐릭터 자동 생성. 같은 트랜잭션이라 캐릭터 생성이 실패하면 회원가입도 취소된다
        // characterService.createFor(saved);

        return saved;

    };

    /**
     * 아이디, 닉네임, 이메일 중복 검사.
     * 어느 칸이 겹쳤는지 알려줘야 사용자가 고칠 수 있다.
     *
     * EXTENSION: 두 사람이 "동시에" 같은 이메일로 가입하면 둘 다 이 검사를 통과할 수 있다.
     *            그때는 DB 의 UNIQUE 가 두 번째를 막고 DataIntegrityViolationException 이 난다.
     *            필요하면 Controller 에서 그 예외도 잡아 "이미 사용 중" 메시지로 바꾼다.
     */
    private void validateDuplicate(String username, String nickname, String email) {
        if (memberRepository.existsByUsername(username)) {
            throw new IllegalStateException("이미 사용 중인 아이디입니다.");
        }
        if (memberRepository.existsByNickname(nickname)) {
            throw new IllegalStateException("이미 사용 중인 닉네임입니다.");
        }
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }
    }

    /** 회원 탈퇴 */
    // EXTENSION: 캐릭터, 가방, 게시글이 member 를 가리키게 되면 그냥 delete 는 외래키 에러가 난다.
    //            그때 "탈퇴 시 데이터를 어떻게 할지(같이 삭제 / 익명 처리)" 를 정해야 한다
    public void withdrawMember(String username) {
        memberRepository.findByUsername(username).ifPresent(memberRepository::delete);
    }
}
