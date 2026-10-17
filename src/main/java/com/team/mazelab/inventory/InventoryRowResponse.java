package com.team.mazelab.inventory;


import com.team.mazelab.item.Grade;
import com.team.mazelab.item.ItemSlot;

/**
 * 가방 한 줄의 응답 모양 (프론트와 약속한 5칸).
 *   {"id":500,"itemId":1,"grade":"D","equippedSlot":null,"listed":false}
 *
 * 이름, 능력치는 보내지 않는다 → 프론트가 /api/items 에서 받은 도감으로 itemId 를 찾아서 그린다
 *   (같은 정보를 두 번 보내지 않는다 = 응답이 가볍고, 도감이 바뀌어도 여기는 안 바뀐다)
 */
public record InventoryRowResponse(Long id, Long itemId, Grade grade,
                                   ItemSlot equippedSlot, boolean listed) {

    public static InventoryRowResponse from(InventoryItem row){
        // row.getItem().getId() : LAZY 라도 id 는 이미 알고 있어서 SELECT 가 일어나지 않는다
        return new InventoryRowResponse(row.getId(), row.getItem().getId(), row.getGrade(),
                row.getEquippedSlot(), row.isListed());
    }
}
