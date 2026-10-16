package com.team.mazelab.activity;

import com.team.mazelab.character.CharacterService;
import com.team.mazelab.character.GameCharacter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 활동 기록 조회 API (읽기 전용).
 * 프로토타입의 "원정 기록" 화면이 이 데이터를 쓴다.
 *
 * @Transactional(readOnly = true) : ActivityResponse.from() 이 LAZY 인 item 을 읽기 때문에
 *                                   DB 연결이 열려 있는 동안 응답 상자로 바꿔야 한다
 */
@RestController
@RequestMapping("/api/activity")
@Transactional(readOnly = true)
public class ActivityApiController {

    private final ActivityLogRepository activityLogRepository;
    private final CharacterService characterService;

    public ActivityApiController(ActivityLogRepository activityLogRepository, CharacterService characterService) {
        this.activityLogRepository = activityLogRepository;
        this.characterService = characterService;
    }

    /** GET /api/activity : 로그인한 내 캐릭터의 최근 100개 (최신순) */
    @GetMapping
    public List<ActivityResponse> recent(@AuthenticationPrincipal UserDetails user) {
        GameCharacter me = characterService.findByUsername(user.getUsername());
        return activityLogRepository.findTop100ByCharacterOrderByIdDesc(me).stream()
            .map(ActivityResponse::from)
            .toList();
    }
}
