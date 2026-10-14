-- ============================================================
-- [전원 합의 후 단 한 번] 공유 DB 비우기               실행 순서: 0
--
-- ⚠️⚠️⚠️ 되돌릴 수 없다 ⚠️⚠️⚠️
-- 공유 DB(pr1_team1db)에 있는 예전 테이블과 데이터를 "영원히" 지운다.
--
-- 실행 전 체크 (하나라도 아니면 실행하지 않는다)
--   [ ] 셋 다 단톡에서 "지워도 된다"고 답했다
--   [ ] (필요하면) 강사님께 공유 DB 를 비워도 되는지 확인했다
--   [ ] 예전 프로젝트 서버를 켜 둔 사람이 없다
--   [ ] DBeaver 왼쪽에서 선택된 DB 가 pr1_team1db 인지 확인했다
--
-- 순서: 다른 테이블을 가리키는 쪽(자식)부터 지운다. member 가 맨 마지막.
-- IF EXISTS : 테이블이 없으면 에러 없이 넘어간다 (만든 적 없는 테이블도 안전)
-- ============================================================

-- 미로 데이터 테이블 (혹시 시험으로 만들었을 경우)
DROP TABLE IF EXISTS member_region_progress;
DROP TABLE IF EXISTS member_stats;
DROP TABLE IF EXISTS stage_best;
DROP TABLE IF EXISTS play_attempt;
DROP TABLE IF EXISTS stage;
DROP TABLE IF EXISTS brain_region;

-- 독서관 테이블
DROP TABLE IF EXISTS board;
DROP TABLE IF EXISTS rental;
DROP TABLE IF EXISTS loan_status;
DROP TABLE IF EXISTS book;

-- 회원 (맨 마지막)
DROP TABLE IF EXISTS member;

-- 확인: 결과가 비어 있어야 한다. 남은 테이블이 있으면 단톡에 이름을 알린다
SHOW TABLES;
