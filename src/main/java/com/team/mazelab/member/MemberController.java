package com.team.mazelab.member;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 로그인 페이지 이동
    @GetMapping("/login")
    public String loginPage() {
        return "member/login"; // templates/member/login.html
    }

    // 회원가입 페이지 이동
    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/signup"; // templates/member/signup.html
    }

    // 회원가입 처리 (비밀번호 일치 여부 및 검증 적용)
    @PostMapping("/signup")
    public String signup(@Valid MemberFormDto memberFormDto, BindingResult bindingResult) {

        // 1. 비밀번호와 비밀번호 확인이 서로 일치하는지 검사
        if (!memberFormDto.getPassword().equals(memberFormDto.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "passwordInCorrect", "비밀번호가 일치하지 않습니다.");
        }

        // 2. 검증 에러가 있으면 다시 회원가입 페이지로
        if (bindingResult.hasErrors()) {
            return "member/signup";
        }

        try {
            memberService.saveMember(memberFormDto);
        } catch (IllegalStateException e) {
            bindingResult.reject("signupError", e.getMessage());
            return "member/signup";
        }

        return "redirect:/login"; // 가입 성공 시 로그인 페이지로 리다이렉트
    }
}
