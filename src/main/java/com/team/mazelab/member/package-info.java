/**
 * ===== [3번 회원] 영토 =====   담당: ________
 *
 * 독서관에서 가져온 것 (= 1번, 2번과의 "약속". 칸 이름 함부로 바꾸지 않는다!):
 *   Member.java              username / password / name / role / birthDate / gender
 *   Role.java                USER / ADMIN
 *   MemberRepository.java    findByUsername, existsByUsername
 *   MemberDetailsService     DB 회원으로 로그인
 *   MemberService            회원가입 규칙
 *   MemberController         주소: /login, /signup
 *   MemberDataInitializer    연습 회원 user / admin (비번 1234)
 *
 * ★ 2번과의 약속 (docs/DATA_CONTRACT.md B-1):
 *   2번의 RegionProgressService 가 생기면, 회원가입(MemberService.saveMember)과
 *   MemberDataInitializer 에서 저장 직후 regionProgressService.initFor(member) 를 한 줄 부른다.
 *
 * 화면: templates/member/  (login.html, signup.html)
 */
package com.team.mazelab.member;
