-- ============================================================
-- [3번 회원] 회원 테이블                             실행 순서: 1
-- 짝꿍 코드: src/main/java/com/team/mazelab/member/Member.java
--
-- ★ 00 바로 다음에 실행한다. 앞으로 만들 테이블이 전부 member 를 가리킨다 (외래키)
-- 독서관 시절 01_member.sql + changes/생년월일·성별 추가를 한 파일로 합쳤다.
--
-- 번역 메모
--   enum Role (@Enumerated STRING)  → VARCHAR(20)   "USER" / "ADMIN"
--   LocalDate birthDate             → DATE          칸 이름 birth_date
-- ============================================================
CREATE TABLE member (
    id          BIGINT        NOT NULL AUTO_INCREMENT,   -- Long id + IDENTITY
    username    VARCHAR(30)   NOT NULL,                  -- 로그인 아이디
    password    VARCHAR(255)  NOT NULL,                  -- BCrypt 암호 (원문 저장 금지)
    name        VARCHAR(20)   NOT NULL,                  -- 화면에 보일 이름
    role        VARCHAR(20)   NOT NULL,                  -- USER / ADMIN
    birth_date  DATE          NULL,                      -- 생년월일 (연령대 비교는 본인이 공개할 때만)
    gender      VARCHAR(10)   NULL,                      -- "M" / "F"
    PRIMARY KEY (id),
    UNIQUE KEY uk_member_username (username)             -- 아이디 중복 금지
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
