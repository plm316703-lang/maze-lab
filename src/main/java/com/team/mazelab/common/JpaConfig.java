package com.team.mazelab.common;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA 설정 모음.
 * @EnableJpaAuditing : BaseTimeEntity 의 @CreatedDate / @LastModifiedDate 를 켜는 스위치.
 *                      이게 없으면 시각이 null 로 저장되려다 NOT NULL 에러가 난다.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
