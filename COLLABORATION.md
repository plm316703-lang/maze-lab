# 🤝 협업 규칙 (셋 다 지킨다)

## 1. 큰 그림

```
[1번 미로 게임] ──한 판 결과(JSON)──▶ [2번 데이터] ──테이블 읽기──▶ [3번 랭킹·내 기록]
       ▲                                  ▲
       └──── 스테이지 목록(JSON) ──────────┘
                                          │ initFor(member)
[3번 회원] ── Member 를 빌려줌 ────────────┘  (가입할 때 한 줄)
```

- 1번과 2번은 **JSON 모양**으로만 만난다. 서로의 자바 코드를 몰라도 된다
- 2번과 3번은 **테이블과 메서드 이름**으로 만난다. 3번은 2번 테이블을 읽기만 한다
- 약속의 자세한 내용: [docs/DATA_CONTRACT.md](docs/DATA_CONTRACT.md)

## 2. 땅 규칙

- **내 폴더 = 내 땅.** 마음대로 고친다
- **남의 폴더는 말하고** 고친다
- 주소 앞머리: 1번 `/maze/**` · 2번 `/api/**` · 3번 `/login` `/signup` `/me` `/rank` `/boards` · 관리자 `/admin/**`

## 3. 공용 파일 (충돌 1순위)

| 파일 | 규칙 |
|---|---|
| `config/SecurityConfig.java` | `// ===== [N번] =====` **내 구역 안에만** 줄 추가 |
| `templates/fragments/header.html` | 내 메뉴 자리에 **한 줄** |
| `templates/home.html` | **내 카드만** 고침 |
| `static/css/style.css` | 맨 아래 **내 구역에만** 추가 |
| `build.gradle`, `settings.gradle`, `application.yml` | **단톡에 말하고** |
| `member/Member.java` | **3번만**. 바꾸면 단톡에 알림 |
| `docs/DATA_CONTRACT.md` | 약속을 바꾸는 사람이 고치고 단톡에 알림 |

## 4. 셋 다 같은 모양

- 파일 순서: Entity → Repository → Form/DTO → Service → Controller → html
- 규칙 검사는 Service. 어기면 `IllegalArgumentException` / `IllegalStateException`
- Entity 에 setter 없음. 값은 이름 있는 메서드로만 바꾼다 (예: `recordStars()`)
- `@PathVariable("id")`, `@RequestParam("x")` 이름 꼭 적기
- form 은 `th:action`, JS POST 는 CSRF 헤더
- 새 화면 맨 위에 `<div th:replace="~{fragments/header :: header}"></div>`
- 로그인한 사람: `@AuthenticationPrincipal UserDetails user` → `user.getUsername()`
  화면 form 에서 username 을 받지 않는다 (남의 이름으로 기록을 남길 수 있게 됨)

## 5. DB 규칙

- 공유 DB 는 `ddl-auto: validate` 만. 테이블은 `db/` SQL 로 사람이 만든다
- **Entity 를 main 에 합치기 직전에** 그 SQL 을 공유 DB 에 실행한다. 순서가 바뀌면 셋 다 서버가 안 켜진다
- 자세히: [db/README.md](db/README.md)

## 6. 깃 매일 흐름

```bash
git checkout main && git pull                 # 1. 최신 받기
git checkout -b feature/maze-board            # 2. 내 가지 (처음 한 번)  예) feature/score-db, feature/rank
git add . && git commit -m "미로 화면 추가"    # 3. 작게 자주
git checkout main && git pull                 # 4. 올리기 전 main 받아서
git checkout feature/maze-board && git merge main   #    내 가지에 합쳐 보기
git push origin feature/maze-board            # 5. 올리고 GitHub 에서 PR
```

- `main` 에 직접 push 금지
- 서버 안 켜지는 코드, 테스트 빨간불은 PR 금지
- 충돌 나면: `<<<` `===` `>>>` 줄 지우고 **보통 둘 다 살린다**

## 7. 사람 규칙

- 하루 한 번 단톡 3줄: **어제 한 것 / 오늘 할 것 / 막힌 것**
- 혼자 **30분** 막히면 → 에러 메시지 통째로 단톡에
- 남의 코드를 대신 짜 주지 않는다. 설명하고 리뷰한다 (발표는 각자 자기 부분)
