package com.team.mazelab.inventory;


import com.team.mazelab.character.GameCharacter;
import com.team.mazelab.item.ItemSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


/**
 * 가방 창고지기.
 *
 * ★ findByIdAndCharacter : "번호" 와 "주인" 을 같이 건다
 *   → WHERE id = ? AND character_id = ?
 *   남의 가방 번호를 넣으면 결과가 없다 = 남의 아이템을 장착할 방법이 원천 차단된다
 *   (findById 만 쓰면 남의 아이템도 찾아져서 따로 주인 검사를 해야 하고, 빼먹으면 보안 구멍)
 */

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    //내 가방 전체(오래된 순)
    List<InventoryItem>findByCharacterOrderByIdAsc(GameCharacter character);
//id와 character을 동 시 조건에 사용하여, 해킹 및 조작을 반지
    /** 내 가방의 특정 한 줄 */
    Optional<InventoryItem> findByIdAndCharacter(Long id, GameCharacter character);

    /** 지금 그 칸에 장착 중인 것 (없으면 empty) */
    Optional<InventoryItem> findByCharacterAndEquippedSlot(GameCharacter character, ItemSlot slot);
}
