# 🤝 데이터 약속 (1번 ↔ 2번 ↔ 3번)

셋이 서로의 코드를 기다리지 않으려면 **주고받는 데이터 모양**을 먼저 못 박는다.
이 문서가 바뀌면 단톡에 알린다. 바꾸는 사람이 이 문서도 같이 고친다.

```
[1번 미로 게임]  ──한 판 결과(JSON)──▶  [2번 데이터]  ──테이블·메서드──▶  [3번 회원·랭킹·게시판]
       ▲                                     │
       └──────── 스테이지 목록(JSON) ─────────┘
```

---

## A. 1번 ↔ 2번: 게임과 데이터

주소 앞머리: 1번 화면 `/maze/**`, 2번 API `/api/**`

### A-1. 스테이지 목록 `GET /api/stages` (2번 → 1번)

```json
[
  {
    "id": 4,
    "title": "냄새 기억",
    "regionCode": "SMELL",
    "regionName": "후각",
    "mazeSize": 12,
    "mazeSeed": 41,
    "botMinTokens": 38,
    "targetSeconds": 22,
    "myBestStars": 3,
    "heartsLeft": 3
  }
]
```

- 1번은 `mazeSize`, `mazeSeed` 로 미로를 만든다. **같은 씨앗 → 같은 미로**여야 한다
- `botMinTokens` 는 1번 생성기의 BFS 최단 이동과 같아야 한다. 다르면 2번에게 알려 `stage` 값을 고친다
- 시간 제한은 `targetSeconds × 2`. 넘으면 실패

### A-2. 한 판 결과 `POST /api/plays` (1번 → 2번)

**보내는 것** (게임이 아는 것만)
```json
{
  "stageId": 4,
  "moves": 38,
  "success": true,
  "seconds": 21
}
```

| 칸 | 규칙 |
|---|---|
| `stageId` | 있는 스테이지 번호 |
| `moves` | 1 이상. 벽에 막혀 못 간 것은 세지 않는다 |
| `success` | 출구 도착 `true` / 포기·시간 초과 `false` |
| `seconds` | 첫 이동부터 끝까지, 초 (반올림 정수) |

**보내지 않는 것** (서버가 정한다): 회원(로그인한 사람), FlyBot 최단, 목표 시간, 회차 안 몇 번째 시도, 끝난 시각, 별

**받는 것**
```json
{
  "stars": 3,
  "conditions": { "success": true, "shortest": true, "firstTry": true },
  "heartsLeft": 3,
  "challengeNo": 2,
  "abilities": {
    "measuring": false,
    "speed": 73, "efficiency": 92, "accuracy": 92, "persistence": null,
    "total": null, "totalGrade": null
  },
  "brainGrowth": 50,
  "level": 5
}
```
- 능력치 값이 `null` 이면 화면에 "측정 중"
- 등급(S~D)은 점수로 화면에서 바꿔도 되고, `Grade.from(점수)` 결과를 받아도 된다

### A-3. 처음부터 `POST /api/stages/{id}/restart` (1번 → 2번)
하트를 3개로 되돌리고 새 회차를 시작한다. 받는 것: `{ "heartsLeft": 3, "challengeNo": 3 }`

### A-4. 알고 가야 할 한계 (v1)
`moves`, `seconds` 는 브라우저가 보낸 값을 믿는다. 서버는 범위만 검사한다
(성공인데 `moves < botMinTokens` 이면 거절, `seconds` 가 0 미만이면 거절).
조작 방지(서버가 시작 시각 기록, 이동 기록 재검사)는 다음 단계.

### A-5. JavaScript 에서 보낼 때 (CSRF)
Spring Security 때문에 `fetch` POST 는 CSRF 토큰이 없으면 **403**.
게임 화면 HTML 의 `<head>` 에 넣고:
```html
<meta name="_csrf" th:content="${_csrf.token}">
<meta name="_csrf_header" th:content="${_csrf.headerName}">
```
보낼 때 헤더로 붙인다:
```javascript
const token  = document.querySelector('meta[name="_csrf"]').content;
const header = document.querySelector('meta[name="_csrf_header"]').content;
fetch('/api/plays', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json', [header]: token },
  body: JSON.stringify({ stageId, moves, success, seconds })
});
```

---

## B. 2번 ↔ 3번: 데이터와 회원·랭킹

### B-1. 가입할 때 (3번이 호출)
가입 처리 **같은 트랜잭션 안에서** 한 줄:
```java
regionProgressService.initFor(member);   // 영역마다 0% 줄 만들기 (2번 제공)
```
빠뜨리면 그 회원은 미로를 깨도 뇌가 밝아지지 않는다 (2번 쪽에서 에러).
연습 회원을 심는 `MemberDataInitializer` 에서도 저장 직후 같은 한 줄을 부른다.
자리는 `MemberService.saveMember()` 에 TODO 주석으로 표시해 두었다.

### B-2. 랭킹 (3번이 화면, 2번이 조회 메서드)
규칙: `brain_growth` 내림차순 → 같으면 `total` 내림차순. `total` 이 `NULL`(측정 중)이면 제외.

2번이 `MemberStatsRepository` 에 만들어 둔다:
```java
List<MemberStats> findByTotalIsNotNullOrderByBrainGrowthDescTotalDesc(Pageable pageable);
```
3번은 호출만:
```java
memberStatsRepository.findByTotalIsNotNullOrderByBrainGrowthDescTotalDesc(PageRequest.of(0, 20));
```

### B-3. 내 기록, 프로필 (3번이 읽기)
| 보여 줄 것 | 읽는 곳 |
|---|---|
| 능력치 4개, 종합 | `member_stats` (`MemberStats`) |
| 등급 | `Grade.from(점수)` (null 이면 "측정 중") |
| 레벨 | `brainGrowth / 10` |
| 영역별 뇌 밝기 | `member_region_progress` (`MemberRegionProgressRepository.findByMember`) |
| 최근 판 목록 | `play_attempt` (`PlayAttemptRepository`) |

3번은 2번 테이블에 **쓰지 않는다.** 바꿀 게 있으면 2번 메서드를 부탁한다.

### B-4. 공개 범위 (3번 담당, 나중에)
회원이 공개를 끈 항목은 3번 화면에서 거른다. 2번 테이블에는 공개 여부 칸이 없다.
