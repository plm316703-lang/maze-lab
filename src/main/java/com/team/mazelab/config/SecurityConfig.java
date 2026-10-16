package com.team.mazelab.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;


/**
 * ★★★ 공용 파일 ★★★  문지기 규칙책.
 *
 * 규칙은 위에서부터 검사한다. 먼저 맞는 규칙이 이긴다.
 * anyRequest() 는 "나머지 전부" 라서 항상 맨 마지막!
 *
 * 주소 앞머리 약속
 *   화면   : /login /signup /game (게임 화면)
 *   데이터 : /api/**  (JSON. 백엔드 담당)
 *   관리자 : /admin/**
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 로그인 안 한 사람이 "화면"을 열면 → 로그인 화면으로 보낸다 (기존 동작)
        LoginUrlAuthenticationEntryPoint loginRedirect = new LoginUrlAuthenticationEntryPoint("/login");

        http
            .authorizeHttpRequests(auth -> auth
                // ===== [공용] =====
                .requestMatchers("/", "/css/**", "/js/**", "/error").permitAll()
                //=== [회원] =====
                .requestMatchers("/signup", "/login").permitAll()
                // ===== [관리자] =====
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // ===== 항상 맨 마지막: 나머지(/api/**, /game 포함)는 로그인해야 들어옴 =====
                .anyRequest().authenticated()
            )

            // ★ 로그인 안 한 상태로 막힌 곳에 왔을 때 "어떻게 거절할지"
            //   - /api/** : 401 + JSON. fetch 는 302 를 받으면 로그인 화면 HTML 을 JSON 으로 읽으려다 깨진다
            //   - 그 외    : 지금처럼 /login 으로 이동
            .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
                if (request.getRequestURI().startsWith("/api/")) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);   // 401
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                        "{\"code\":\"UNAUTHORIZED\",\"message\":\"로그인이 필요합니다.\"}");
                } else {
                    loginRedirect.commence(request, response, authException);
                }
            }))
            .formLogin(form -> form
                .loginPage("/login")              // 우리가 만든 로그인 화면
                .defaultSuccessUrl("/", true)     // EXTENSION: 게임 화면이 생기면 "/game" 으로
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
