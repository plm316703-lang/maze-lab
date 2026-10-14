package com.team.mazelab.member;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public MemberDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. DB에서 username으로 회원 찾기 (Optional 처리)
        Member member = memberRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다: " + username));

        // 2. 스프링 시큐리티가 이해할 수 있는 User 객체로 변환해서 리턴
        return org.springframework.security.core.userdetails.User.builder()
            .username(member.getUsername())
            .password(member.getPassword())
            .authorities("ROLE_" + member.getRole().name()) // ROLE_ 접두사 명시
            .build();
    }
}
