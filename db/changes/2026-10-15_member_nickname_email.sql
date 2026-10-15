-- ============================================================
-- [변경] member : name → nickname, email 추가, 생년월일·성별 삭제
-- 날짜: 2026-10-15   작성: 박민혁 (3번 확인 받음)
-- 짝꿍 코드: src/main/java/com/team/mazelab/member/Member.java
--
-- ⚠ 공유 DB 실행 시점: PR 을 합치기 "직전".
--   실행하는 순간 pull 안 한 팀원의 서버는 validate 실패로 안 켜진다 → 단톡 예고 필수
-- ⚠ 01_member.sql 은 고치지 않는다 (이미 실행한 파일). 새로 DB 를 만드는 사람은 01 → 이 파일 순서로 실행
-- ============================================================

-- ① 테스트 회원 비우기
--    email 을 NOT NULL + UNIQUE 로 추가하면 기존 회원의 email 이 전부 빈 값('')이 되고,
--    빈 값끼리 겹쳐서 UNIQUE 에러가 난다. 지금은 테스트 데이터뿐이라 비우고 시작한다.
--    (연습 계정 user / admin 은 서버를 켜면 MemberDataInitializer 가 다시 만든다)
--    DBeaver 가 "WHERE 없는 DELETE" 경고를 띄우면, 연결된 DB 를 한 번 더 확인하고 진행


DELETE FROM member;

-- ② ~ ④ : ALTER 한 번에 여러 변경을 쉼표로 이어서
ALTER TABLE member
    CHANGE COLUMN name nickname VARCHAR(20) NOT NULL,        -- 이름 바꾸기 (CHANGE 는 타입까지 다시 적어야 함)
    ADD COLUMN email VARCHAR(100) NOT NULL AFTER nickname,   -- AFTER : 칸 위치 (보기 좋게 nickname 뒤에)
    DROP COLUMN birth_date,                                  -- 생년월일 삭제
    DROP COLUMN gender,                                      -- 성별 삭제
    ADD UNIQUE KEY uk_member_nickname (nickname),            -- 닉네임 중복 금지 (자유시장에 이름이 보이니까)
    ADD UNIQUE KEY uk_member_email (email);                  -- 이메일 중복 금지

-- 확인 : 6칸이어야 한다 (id, username, password, nickname, email, role)
DESC member;
