package com.team.mazelab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 서버 시작 버튼.
 *
 * - main() 실행 → 스프링이 깨어남 → 이 파일이 있는 패키지(com.team.mazelab) 아래를 전부 뒤짐
 * - @Controller, @Service, @Repository, @Configuration 이 붙은 것을 찾아서 객체(빈)로 만들어 둠
 *
 * 규칙! 새 기능 패키지(maze, score, rank 등)는 반드시 com.team.mazelab 아래에 만든다.
 *        밖에 만들면 스프링이 못 찾는다.
 */
@SpringBootApplication
public class MazeLabApplication {
    public static void main(String[] args) {
        SpringApplication.run(MazeLabApplication.class, args);
    }
}
