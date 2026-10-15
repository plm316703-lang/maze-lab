package com.team.mazelab.character;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 캐릭터 JSON API. 프론트는 fetch 로 부른다.
 *
 * @RestController = @Controller + @ResponseBody
 *   → return 값이 "화면 이름"이 아니라 그대로 JSON 으로 바뀌어 나간다
 *
 * 주소 규칙 : 데이터 API 는 전부 /api/** 로 시작
 */
@RestController
@RequestMapping("/api/characters")
public class CharacterApiController {

    private final CharacterService characterService;

    public CharacterApiController(CharacterService characterService){
        this.characterService = characterService;
    }

    /**
     * GET /api/characters/me : 로그인한 내 캐릭터
     *
     * @AuthenticationPrincipal : 스프링 시큐리티가 "지금 로그인한 사람"을 넣어 준다
     * ★ 요청에서 characterId 나 username 을 받지 않는다 → 남의 캐릭터를 조회할 방법 자체가 없음
     */

    @GetMapping("/me")
    public CharacterResponse me(@AuthenticationPrincipal UserDetails user){
        return characterService.getMyCharacter(user.getUsername());
    }
    // EXTENSION: 로그인 안 한 상태로 부르면 지금은 로그인 페이지로 302 이동한다.
    //            프론트 방식이 정해지면 /api/** 는 401 + JSON 에러로 바꾼다 (SecurityConfig)
}
