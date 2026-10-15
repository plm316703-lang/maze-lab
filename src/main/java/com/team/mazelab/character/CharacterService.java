package com.team.mazelab.character;


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

    public CharacterService(GameCharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }
    /** 회원가입 직후 캐릭터 만들기. MemberService 가 같은 트랜잭션 안에서 부른다 */
    public GameCharacter createFor(Member member){
        if(characterRepository.existsByMember(member)){
            throw new IllegalStateException("이미 캐릭터가 있습니다.");
        }
        return characterRepository.save(GameCharacter.createFor(member));
        // EXTENSION (Step 3): 시작 장비 지급 (D 등급 무기 1개를 가방에)
        // EXTENSION (Step 4): idle_progress 2줄 생성 (MINE, FISH)
    }

    /** 없으면 만든다. 연습 계정처럼 "캐릭터 기능이 생기기 전에 가입한 회원"용 */
    public void ensureCharacter(Member member) {
        if (!characterRepository.existsByMember(member)) {
            characterRepository.save(GameCharacter.createFor(member));
        }
    }

    /** 로그인한 사람의 캐릭터. 다른 Service 들이 가장 많이 부를 메서드 */
    @Transactional(readOnly = true)
    public GameCharacter findByUsername(String username){
        return characterRepository.findByMember_Username(username)
            .orElseThrow(() -> new IllegalStateException("캐릭터가 없습니다. (" + username + ")"));
    }

    /** 내 캐릭터 정보 → 응답 상자 (트랜잭션 안에서 DTO 로 바꾼다) */
    @Transactional(readOnly = true)
    public CharacterResponse getMyCharacter(String username) {
        return CharacterResponse.from(findByUsername(username));
    }


}
