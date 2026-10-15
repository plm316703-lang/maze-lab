package com.team.mazelab.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


/**
 * 회원가입 화면의 입력값을 담는 상자.
 * 어노테이션 = 입력 규칙. Controller 의 @Valid 가 이 규칙을 검사하고,
 * 어긴 칸은 BindingResult 에 담겨 화면에 빨간 글씨로 돌아간다.
 *
 * ★ 보안: 예전에 있던 role 칸을 뺐다.
 *   DTO 에 role 이 있으면, 누군가 개발자 도구로 form 에 role=ADMIN 을 끼워 보내서
 *   관리자로 가입할 수 있다. 권한은 사용자가 고르는 값이 아니라 서버가 정하는 값이다.
 */
public class MemberFormDto {

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상, 20자 이하로 입력해주세요.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상, 20자 이하로 입력해주세요.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수 입력 값입니다.")
    private String passwordConfirm;   // DB 에는 저장 안 함. Controller 에서 password 와 같은지만 비교

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상, 20자 이하로 입력해주세요.")
    private String nickname;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")       // abc@def 모양인지 검사
    @Size(max = 100, message = "이메일은 100자 이하로 입력해주세요.")
    private String email;

    // Getter & Setter : Thymeleaf 의 th:field 와 스프링의 값 채우기가 setter 를 쓴다
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPasswordConfirm() { return passwordConfirm; }
    public void setPasswordConfirm(String passwordConfirm) { this.passwordConfirm = passwordConfirm; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
