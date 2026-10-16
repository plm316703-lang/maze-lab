package com.team.mazelab.character;

import com.team.mazelab.activity.ActivityLogger;
import com.team.mazelab.activity.ActivityType;
import com.team.mazelab.activity.Delta;
import com.team.mazelab.member.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 캐릭터 규칙을 지키는 곳.
 * 다른 기능(광산, 강화, 자유시장)은 캐릭터가 필요하면 findByUsername() 하나만 알면 된다.
 */
@Service
@Transactional
public class CharacterService {

    private final GameCharacterRepository characterRepository;
    private final ActivityLogger activityLogger;              // ★ Step 3-2 추가

    public CharacterService(GameCharacterRepository characterRepository, ActivityLogger activityLogger) {
        this.characterRepository = characterRepository;
        this.activityLogger = activityLogger;
    }

    /** 회원가입 직후 캐릭터 만들기. MemberService 가 같은 트랜잭션 안에서 부른다 */
    public GameCharacter createFor(Member member) {
        if (characterRepository.existsByMember(member)) {
            throw new IllegalStateException("이미 캐릭터가 있습니다.");
        }
        return createAndLog(member);

        // EXTENSION (Step 3-3): 시작 장비 지급 (D 등급 무기 1개를 가방에)
        // EXTENSION (Step 5):   idle_progress 2줄 생성 (MINE, FISH)
    }

    /** 없으면 만든다. 연습 계정처럼 "캐릭터 기능이 생기기 전에 가입한 회원"용 */
    public void ensureCharacter(Member member) {
        if (!characterRepository.existsByMember(member)) {
            createAndLog(member);
        }
    }

    /**
     * 캐릭터 저장 + "원정대 합류" 기록. createFor 와 ensureCharacter 가 같이 쓴다 (중복 코드 제거)
     * 시작 골드도 "골드가 생긴 사건"이라 기록에 남긴다 → 나중에 골드 흐름을 처음부터 추적할 수 있다
     */
    private GameCharacter createAndLog(Member member) {
        GameCharacter saved = characterRepository.save(GameCharacter.createFor(member));
        activityLogger.log(saved, ActivityType.CHARACTER_CREATED,
            Delta.gold(GameCharacter.START_GOLD),
            "원정대 합류 · 시작 골드 " + GameCharacter.START_GOLD);
        return saved;
    }

    /** 로그인한 사람의 캐릭터. 다른 Service 들이 가장 많이 부를 메서드 */
    @Transactional(readOnly = true)
    public GameCharacter findByUsername(String username) {
        return characterRepository.findByMember_Username(username)
            .orElseThrow(() -> new IllegalStateException("캐릭터가 없습니다. (" + username + ")"));
    }

    /** 내 캐릭터 정보 → 응답 상자 (트랜잭션 안에서 DTO 로 바꾼다) */
    @Transactional(readOnly = true)
    public CharacterResponse getMyCharacter(String username) {
        return CharacterResponse.from(findByUsername(username));
    }
}
