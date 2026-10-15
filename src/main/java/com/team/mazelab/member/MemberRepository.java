package com.team.mazelab.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 회원 창고지기. DB 에 넣고 꺼내는 일만 한다.
 *
 * - JpaRepository<Member, Long> 을 상속하면 save(), findById(), findAll(), delete() 가 공짜로 생김
 * - 메서드 이름만 규칙대로 지으면 SQL 이 자동으로 만들어진다
 *     findByUsername    → SELECT * FROM member WHERE username = ?
 *     existsByNickname  → SELECT 1 ... WHERE nickname = ?  (있으면 true)
 * interface 인데 구현 클래스 안 만들어도 됨. 스프링이 대신 만들어 준다.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    // Optional : "있을 수도, 없을 수도" 상자. null 대신 쓴다 → NullPointerException 예방
    Optional<Member> findByUsername(String username);

    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);         // 회원가입 때 이메일 중복 검사
}
