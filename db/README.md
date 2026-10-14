# 🗄️ 공유 DB 규칙 + 실행 기록

공유 DB: `1.220.247.76:13306` / `pr1_team1db` (셋이 같이 씀, MySQL 8)

## 규칙 6개

1. 스프링은 `ddl-auto: validate` 만. **create / update 금지** (남의 테이블이 지워진다)
2. 테이블은 **이 폴더의 SQL** 로 사람이 만든다 (DBeaver 에서 실행)
3. **내 테이블만** 만들고 고친다. 남의 테이블 `DROP / ALTER / TRUNCATE` 금지 (00 번 파일만 예외, 전원 합의)
4. 이미 실행한 SQL 파일은 고치지 않는다. 바꾸려면 `changes/날짜_설명.sql` 새 파일
5. **추가만 자유롭게.** 칸 이름 변경 / 삭제 / 타입 변경은 단톡에서 셋이 합의 후
6. DBeaver 는 Auto 커밋이다. `DELETE`, `UPDATE` 는 `WHERE` 있는지 두 번 본다

## 지금 있는 파일

| 순서 | 파일 | 테이블 | 주인 | 언제 |
|---|---|---|---|---|
| 1 | 전체 생성 | member 연결 확인 | 박민혁 | 26.10.14

## 앞으로 추가될 파일 (각자 PR 로)

| 번호 | 테이블 | 주인 |
|---|---|---|
| 02 ~ 07 | brain_region, stage, play_attempt, stage_best, member_stats, member_region_progress | 2번 |
| 08 ~ | board 등 | 3번 |

새 테이블은 **그 Entity 를 main 에 합치기 직전에** 공유 DB 에 실행한다.
순서가 바뀌면 validate 실패로 셋 다 서버가 안 켜진다.

## 테이블 고칠 때

```
① 단톡 예고 → ② changes/ 에 SQL + Entity 수정 → ③ 안전한 변경인지 확인
→ ④ DBeaver 실행 → ⑤ 내 서버 validate 통과 → ⑥ PR → ⑦ 단톡 공지
```

## 실행 기록 (공유 DB 에 실행하면 한 줄 추가)

| 날짜 | 파일 | 실행한 사람 | 내용 |
|---|---|---|---|
|  | 00_reset_all.sql |  | 예전 테이블 전부 삭제 |
|  | 01_member.sql |  | member 생성 |
