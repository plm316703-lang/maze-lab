package com.team.mazelab.member;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * 회원 엔티티. DB 테이블 "member" 한 줄 = Member 객체 하나.
 *
 * 칸: id, username, password, nickname, email, role
 * 테이블 변경 기록: db/01_member.sql → db/changes/2026-10-15_member_nickname_email.sql
 *
 * 규칙
 * - 비밀번호는 BCrypt 로 암호화된 글자만 저장한다 (원문 저장 금지)
 * - setter 를 만들지 않는다. 값을 바꿀 일이 생기면 이름 있는 메서드로 (아래 EXTENSION 참고)
 * - 회원 1명 = 캐릭터 1개. 캐릭터 이름은 따로 두지 않고 nickname 을 쓴다
 */
@Entity
@Table(name = "member")
public class Member{
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false) // 암호화된 비밀번호 (BCrypt 결과는 60글자)
    private String password;

    @Column(nullable = false, unique = true, length = 20) // 화면, 게시판, 자유시장에 보일 이름
    private String nickname;

    @Column(nullable = false, unique = true, length = 100) // 이메일. 나중에 비밀번호 찾기 등에 쓸 수 있음
    private String email;

    @Enumerated(EnumType.STRING)  // DB 에 "USER" 글자로 저장 (숫자로 저장하면 순서가 바뀔 때 뜻이 바뀜)
    @JdbcTypeCode(SqlTypes.VARCHAR)                       // MySQL ENUM 타입이 아니라 VARCHAR 칸으로 본다 (validate 통과용)
    @Column(nullable = false, length = 20)
    private Role role;

    // JPA 가 DB 에서 꺼낸 값을 담을 때 쓰는 빈 생성자. 우리가 직접 쓰지 않으니 protected
    protected Member(){}

    /**
     * 회원가입용 생성자.
     * @param encodedPassword 반드시 passwordEncoder.encode() 를 거친 값을 넣는다
     */
    public Member(String username, String encodedPassword, String nickname, String email, Role role) {
        this.username = username;
        this.password = encodedPassword;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
    }

    // EXTENSION: 닉네임 변경, 비밀번호 변경 기능이 생기면 setter 대신 이런 이름 있는 메서드로 추가
    // public void changeNickname(String newNickname) { this.nickname = newNickname; }

    // getter (값 읽기만)
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getNickname() { return nickname; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
}
