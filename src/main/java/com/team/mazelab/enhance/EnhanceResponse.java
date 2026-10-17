package com.team.mazelab.enhance;

import com.team.mazelab.character.CharacterResponse;
import com.team.mazelab.inventory.InventoryRowResponse;
import com.team.mazelab.item.Grade;

import java.util.List;

/**
    * POST /api/inventory/{id}/enhance 의 응답.
 *   {"success":true,"before":"B","after":"A",
     *    "row":{...강화한 장비...},
    *    "inventory":[...가방 전체...],
    *    "character":{"gold":...,"ore":...}}
    *
    * ★ row 와 inventory 를 둘 다 보내는 이유
 *   row       : 프론트가 "강화 결과 연출" 에 쓸 장비 한 줄
 *   inventory : 희귀 재료 줄이 "삭제" 되기 때문에, 가방 전체를 다시 보내야
 *               프론트 가방에서도 재료가 사라진다 (연동 명세 권장 방식)
 */

public record EnhanceResponse(boolean success, Grade before, Grade after,
                              InventoryRowResponse row,
                              List<InventoryRowResponse> inventory,
                              CharacterResponse character) {
}


