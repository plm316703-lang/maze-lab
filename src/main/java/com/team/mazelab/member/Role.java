package com.team.mazelab.member;


/**
 * 회원 계급. 돌멩이 두 개.
 *
 * - USER  : 일반 회원 (미로를 푸는 사람)
 * - ADMIN : 관리자 (스테이지, 게시판 관리)
 *
 * 스프링 시큐리티는 권한 이름 앞에 "ROLE_" 붙은 걸 기대한다.
 * 그래서 getKey() 로 "ROLE_USER" 형태를 돌려준다.
 */
public enum Role {
    USER, ADMIN;

    public String getKey() {
        return "ROLE_" + name();
    }
}
