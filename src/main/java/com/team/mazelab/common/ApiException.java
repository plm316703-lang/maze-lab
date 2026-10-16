package com.team.mazelab.common;


import org.springframework.http.HttpStatus;

/**
 * "상태 코드 + code" 를 함께 들고 다니는 예외.
 *
 * 왜 필요한가?
 *   IllegalStateException 만으로는 "골드 부족(409)" 인지 "이미 팔림(409)" 인지
 *   프론트가 구분할 code 를 실어 보낼 곳이 없다.
 *
 * 사용 예:
 *   throw new ApiException(HttpStatus.NOT_FOUND, "ITEM_NOT_FOUND", "아이템을 찾을 수 없습니다.");
 *
 * RuntimeException 을 상속 → throws 선언 없이 어디서나 던질 수 있고,
 * @Transactional 안에서 던지면 DB 변경이 자동으로 취소(롤백)된다.
 */
public class ApiException extends RuntimeException{

    private final HttpStatus status;
    private final String code;

    public ApiException(HttpStatus status, String code, String message){
        super(message);     //getMessage()로 꺼낼 사람용 메세지
        this.status = status;
        this.code = code;
    }
    public HttpStatus getStatus() { return status; }
    public String getCode() { return code; }
}
