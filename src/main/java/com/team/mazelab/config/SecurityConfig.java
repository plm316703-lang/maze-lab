package com.team.mazelab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * ★★★ 공용 파일 ★★★  문지기 규칙책.
 *
 * 번호마다 "자기 구역"이 있다. 자기 구역 안에만 줄을 추가한다.
 * → 셋이 동시에 고쳐도 서로 다른 줄이라 깃 충돌이 거의 안 난다.
 *
 * 규칙은 위에서부터 검사한다. 먼저 맞는 규칙이 이긴다.
 * anyRequest() 는 "나머지 전부" 라서 항상 맨 마지막!
 *
 * 주소 앞머리 약속
 *   1번 미로  : /maze/**            (화면)
 *   2번 데이터: /api/**             (게임과 JSON 주고받기)
 *   3번 회원  : /login /signup /me  · 랭킹 /rank · 게시판 /boards
 *   관리자    : /admin/**
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // ===== [공용] =====
                .requestMatchers("/", "/css/**", "/js/**", "/error").permitAll()

                // ===== [1번 미로] =====
                // 미로는 기록이 회원에게 쌓여야 하므로 로그인 필요 (아래 anyRequest 가 처리)

                // ===== [2번 데이터] =====
                // /api/** 도 로그인 필요. JS fetch 는 CSRF 토큰을 헤더에 넣는다 (docs/DATA_CONTRACT.md A-5)

                // ===== [3번 회원·랭킹·게시판] =====
                .requestMatchers("/signup", "/login").permitAll()
                // 예) 랭킹을 로그인 없이 보게 하려면: .requestMatchers(HttpMethod.GET, "/rank").permitAll()

                // ===== [관리자] =====
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // ===== 항상 맨 마지막: 나머지는 로그인해야 들어옴 =====
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")              // 우리가 만든 로그인 화면
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );
        return http.build();
    }

    /** 비밀번호 암호화 도구. 모두 이걸 쓴다 */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
