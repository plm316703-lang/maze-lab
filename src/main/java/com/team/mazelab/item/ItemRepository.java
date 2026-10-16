package com.team.mazelab.item;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * 아이템 카탈로그 창고지기.
 *
 * findBySlotNotAndTierInOrderByIdAsc(MATERIAL, [D, C])
 *   → SELECT * FROM item WHERE slot <> 'MATERIAL' AND tier IN ('D','C') ORDER BY id
 *   = 상점 판매 목록
 */
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOrderByIdAsc();

    List<Item> findBySlotNotAndTierInOrderByIdAsc(ItemSlot slot, Collection<Grade> tiers);
}
