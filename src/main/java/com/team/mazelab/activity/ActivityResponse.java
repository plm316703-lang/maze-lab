package com.team.mazelab.activity;


import com.team.mazelab.item.Item;

import java.time.LocalDateTime;

/**
 * GET /api/activity 의 한 줄.
 *   {"id":1,"type":"CHARACTER_CREATED","typeLabel":"원정대 합류","goldDelta":300,"oreDelta":0,"fishDelta":0,
 *    "itemId":null,"itemName":null,"detail":"원정대 합류 · 시작 골드 300","createdAt":"2026-10-16T11:30:00.123"}
 *
 * itemName 을 같이 보내는 이유 : 프론트가 아이템 번호로 카탈로그를 다시 뒤지지 않아도 되게
 */

public record ActivityResponse(Long id, ActivityType type, String typeLabel,
                               int goldDelta, int oreDelta, int fishDelta,
                               Long itemId, String itemName, String detail, LocalDateTime createdAt) {

    /** ⚠ item 이 LAZY 라서 트랜잭션 안에서 부른다 */
    public static ActivityResponse from(ActivityLog log){
        Item it = log.getItem();
        return new ActivityResponse(
            log.getId(), log.getActivityType(), log.getActivityType().label(),
            log.getGoldDelta(), log.getOreDelta(), log.getFishDelta(),
            it == null ? null : it.getId(),        // 아이템이 없는 기록이면 null
            it == null ? null : it.getName(),
            log.getDetail(), log.getCreatedAt());

    }
}
