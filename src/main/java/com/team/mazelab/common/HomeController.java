package com.team.mazelab.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * [공용 / 팀장] 첫 화면.
 * 로그인한 사람 이름은 화면(header.html)에서 sec:authentication 으로 바로 꺼내니까
 * 여기서는 화면만 돌려준다.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }
}
