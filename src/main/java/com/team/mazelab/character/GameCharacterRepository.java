package com.team.mazelab.character;


import com.team.mazelab.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * 캐릭터 창고지기.
 *
 * findByMember_Username : 밑줄(_) = "member 안의 username" 으로 들어가라는 뜻
 *   → SELECT c.* FROM game_character c JOIN member m ON c.member_id = m.id WHERE m.username = ?
 *   로그인한 사람의 username 만 알면 바로 캐릭터를 찾을 수 있다
 */
public interface  GameCharacterRepository extends JpaRepository<GameCharacter, Long>{
    boolean existsByMember(Member member);

    Optional<GameCharacter> findByMember_Username(String username);


}


