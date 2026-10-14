# 🧩 미로 실험실 (maze-lab)

미로를 풀면 한 판의 기록이 쌓이고, 최근 5판으로 능력치와 등급이 매겨지는 사이트.
깬 스테이지만큼 "내 초파리 뇌"의 영역(미각, 후각, 시각)이 밝아진다.

예시 화면: 팀 단톡에 공유한 "미로 실험실" 예시 사이트

| 번호 | 역할 | 영토 (폴더) | 담당 |
|---|---|---|---|
| 1번 | 미로 게임 (스테이지) | `maze/`, `templates/maze/`, `static/js/maze/` | ________ |
| 2번 | 게임 데이터 → 기록·점수·등급 | `score/`, `db/02~07` (추가 예정) | 박민혁 |
| 3번 | 회원, 랭킹, 게시판 | `member/`, `rank/`, `board/`, `templates/member/` `rank/` `board/` | ________ |

## 처음 받은 사람이 읽는 순서

1. **[docs/DEV_SETUP.md](docs/DEV_SETUP.md)** 개발 환경 맞추기 (셋 다 먼저)
2. **[COLLABORATION.md](COLLABORATION.md)** 협업 규칙
3. **[docs/DATA_CONTRACT.md](docs/DATA_CONTRACT.md)** 서로 주고받는 데이터 약속
4. **[db/README.md](db/README.md)** DB 테이블과 실행 순서

## 실행

1. 자바 **21**, IntelliJ 로 이 폴더 열기 → 🐘 Gradle 새로고침
2. 실행 설정 환경 변수에 `DB_USERNAME`, `DB_PASSWORD` (docs/DEV_SETUP.md 4번)
3. `MazeLabApplication` ▶ → http://localhost:8080
4. 연습 계정: `user / 1234`, `admin / 1234`
5. 테스트: `src/test` 우클릭 → Run 'All Tests' → 초록불

## 폴더 지도

```
src/main/java/com/team/mazelab/
├── MazeLabApplication.java   시작 버튼
├── config/SecurityConfig     ★ 공용 문지기 (번호별 구역)
├── common/HomeController     첫 화면
├── maze/    [1번]  미로 화면 컨트롤러
├── score/   [2번]  Entity, 계산기, /api/**
├── member/  [3번]  회원 (독서관에서 가져옴)
├── rank/    [3번]  랭킹, 내 기록
└── board/   [3번]  게시판 (새로 만듦)

src/main/resources/
├── application.yml           공용 설정 (공유 DB, validate)
├── application-local.yml     (선택) 내 컴퓨터 연습 DB
├── templates/
│   ├── fragments/header.html ★ 공용 머리띠 (번호별 메뉴 자리)
│   ├── home.html, error.html
│   └── maze/ member/ rank/ board/
└── static/
    ├── css/style.css         ★ 공용 꾸미기 (맨 아래 번호별 구역)
    └── js/maze/              [1번] 미로 게임 JavaScript

db/        테이블 SQL (사람이 DBeaver 로 실행)
docs/      환경, 데이터 약속 문서
```

## 자주 만나는 함정

| 증상 | 해결 |
|---|---|
| `Schema-validation: missing table` | 그 Entity 의 SQL 이 공유 DB 에 실행됐는지 확인 (db/README.md) |
| JS `fetch` 로 보냈더니 403 | CSRF 토큰을 헤더에 넣기 (docs/DATA_CONTRACT.md A-5) |
| 화면 form 을 눌렀더니 403 | `action` 말고 `th:action` |
| `./gradlew: Permission denied` (맥) | `chmod +x gradlew` |
| 내 컴퓨터만 8080 막힘 | 실행 설정 환경 변수 `PORT=8081` |
