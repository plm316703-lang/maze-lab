package com.team.mazelab.enhance;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * 강화 API.
 *   GET  /api/enhance-rules             강화표 (D → A 순서, 4줄)
 *   POST /api/inventory/{id}/enhance    강화 시도
 *
 * 강화 주소가 /api/inventory/... 아래에 있는 이유 : "가방의 그 장비를" 강화하는 행동이라서
 *   (프론트 services.js 와 같은 주소)
 */

@RestController
@RequestMapping("/api")
public class EnhanceApiController {
    //생성자와 의존성(DI)주입
    private final EnhanceService enhanceService;
    private EnhanceApiController(EnhanceService enhanceService){
        this.enhanceService = enhanceService;
    }

    @GetMapping("/enhance-rules")
    public List<EnhanceRuleResponse> rules() {
        return enhanceService.getRules();
    }

    @PostMapping("/inventory/{id}/enhance")
    public EnhanceResponse enhance(@AuthenticationPrincipal UserDetails user,
                                   @PathVariable("id") Long id) {
        return enhanceService.enhance(user.getUsername(), id);
    }
}
