package com.team.mazelab.common;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * API 에서 터진 예외를 {code, message} JSON 으로 바꿔 주는 곳.
 * 각 Controller 에서 try-catch 를 쓰지 않아도 된다.
 *
 * ★ annotations = RestController.class
 *   "@RestController 에서 난 예외만" 처리한다.
 *   이걸 빼면 회원가입 화면(@Controller)의 에러까지 JSON 으로 바뀌어서
 *   화면 대신 {"code":...} 글자가 뜨는 사고가 난다.
 *
 * 우선순위: 더 구체적인 예외 처리기가 먼저 잡는다 (ApiException → 나머지)
 */
@RestControllerAdvice(annotations = RestController.class)
public class ApiExceptionHandler {

    /**
     * 우리가 직접 던진 예외 : 들고 있는 상태 코드와 code 그대로
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApi(ApiException e) {
        return ResponseEntity.status(e.getStatus())
            .body(new ErrorResponse(e.getCode(), e.getMessage()));
    }

    //요청 값이 잘못됨 (예: 수량 0, Entity 의 requirePositive) → 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("BAD_REQUEST", e.getMessage()));
    }
    /** 지금 상태로는 안 됨 (예: GameCharacter.spendGold 의 골드 부족) → 409 */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleConflict(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse("CONFLICT", e.getMessage()));
    }
    // EXTENSION: @Valid 검사 실패(MethodArgumentNotValidException) → 400 + 어느 칸이 틀렸는지
    //            요청 DTO 에 @NotNull 등을 붙이는 단계(3-3)에서 추가한다

}
