package com.team.mazelab.common;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;


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
    /**
     * @Valid 검사 실패 (예: {"itemId":null}) → 400
     *   여러 칸이 틀렸어도 첫 번째 것만 알려 준다 → "itemId: 널이어서는 안됩니다"
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        FieldError first = e.getBindingResult().getFieldErrors().get(0);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("VALIDATION_FAILED", first.getField() + ": " + first.getDefaultMessage()));
    }

    /** 본문이 아예 없거나 JSON 모양이 깨짐 (예: {"itemId":"abc"}) → 400 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("BAD_REQUEST", "요청 본문(JSON)을 읽을 수 없습니다."));
    }

}
