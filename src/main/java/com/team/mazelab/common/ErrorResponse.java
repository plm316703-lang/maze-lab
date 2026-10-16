package com.team.mazelab.common;


/**
 * 모든 API 실패 응답의 모양 (= 프론트와의 약속).
 *   {"code": "NOT_ENOUGH_GOLD", "message": "골드가 부족합니다."}
 *
 * - code    : 프로그램이 읽는 값. 프론트가 if (code === 'ALREADY_SOLD') 처럼 분기할 때
 * - message : 사람이 읽는 값. 프론트는 그대로 화면에 띄운다 (프로토타입의 flash(e.message) 자리)
 */
public record ErrorResponse(String code, String message) {
}
