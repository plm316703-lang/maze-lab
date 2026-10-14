package com.team.mazelab.member;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDate; // [필수] LocalDate 임포트 추가

/**
 * 회원 엔티티. DB 테이블 "member" 한 줄 = Member 객체 하나.
 * @Entity : "이거 DB 테이블이다" 스프링한테 알림
 * 테이블은 db/01_member.sql 로 사람이 만든다. (공유 DB 라서 ddl-auto: validate)
 * 비밀번호는 절대 그대로 저장 안 한다! 암호화(BCrypt)된 글자만 저장.
 */
@Entity
@Table(name = "member")
public class Member {

    @Id                                                   // 기본키 (줄 번호)
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // 번호 DB가 1,2,3... 알아서 매김
    private Long id;

    @Column(nullable = false, unique = true, length = 30) // 아이디는 겹치면 안 됨
    private String username;

    @Column(nullable = false)                             // 암호화된 비밀번호
    private String password;

    @Column(nullable = false, length = 20)
    private String name;                                  // 화면에 보일 이름

    @Enumerated(EnumType.STRING)                          // DB에 "USER" 글자로 저장 (숫자 X → 순서 바뀌어도 안전)
    @JdbcTypeCode(SqlTypes.VARCHAR)                       // MySQL 전용 ENUM 타입 말고 VARCHAR 칸으로 본다 (validate 통과용)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(name = "birth_date")
    private LocalDate birthDate; // 생년월일

    @Column(length = 10)
    private String gender;       // 성별 ("M" 또는 "F")

    // JPA가 객체 만들 때 쓰는 빈 생성자. 우리가 직접 쓰지 않음 → protected
    protected Member() {}

    // 생년월일과 성별이 포함된 회원가입용 생성자 추가
    public Member(String username, String encodedPassword, String name, LocalDate birthDate, String gender, Role role) {
        this.username = username;
        this.password = encodedPassword;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.role = role;
    }

    // getter
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public Role getRole() { return role; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getGender() { return gender; }
}
