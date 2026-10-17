package com.team.mazelab.inventory;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.mazelab.character.CharacterResponse;

import java.util.List;

/**
 * 가방을 "바꾸는" 요청(구매, 장착, 해제)의 공통 응답 모양.
 *
 * 프론트와의 약속 (연동 명세)
 *   1) character 는 항상 보낸다 → 프론트가 골드 표시를 바로 고친다
 *   2) 한 줄만 바뀌면 row   (구매 : 새 줄 하나)
 *   3) 여러 줄이 바뀌면 inventory 전체 (장착 : 새 장비 + 빠진 기존 장비 = 2줄)
 *
 * @JsonInclude(NON_NULL) : null 인 칸은 JSON 에서 아예 뺀다
 *   → 구매 응답에 "inventory":null 같은 헷갈리는 칸이 나가지 않는다
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public record InventoryActionResponse(InventoryRowResponse row,
                                      List<InventoryRowResponse> inventory,
                                      CharacterResponse  character) {

    //한 줄만 바뀌었을때
    public static InventoryActionResponse ofRow(InventoryRowResponse  row, CharacterResponse character) {
        return new InventoryActionResponse(row, null, character);
    }


        /** 여러 줄이 바뀌었을 때 : 가방 전체를 다시 보낸다 */
        public static InventoryActionResponse ofInventory(List<InventoryRowResponse> inventory,
            CharacterResponse character) {
            return new InventoryActionResponse(null, inventory, character);
        }
    }



