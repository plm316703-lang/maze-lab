# 🛠️ 개발 환경 맞추기 (셋 다 똑같이)

셋의 컴퓨터가 달라도 **같은 자바, 같은 글자 규칙, 같은 DB** 로 돌아가게 맞추는 체크리스트.
위에서부터 순서대로 한다. 다 끝나면 맨 아래 "확인"에 체크.

## 1. 프로그램

| 무엇 | 버전 | 확인 방법 |
|---|---|---|
| JDK | **21** | 터미널 `java -version` → `21.x` |
| IntelliJ IDEA | 최신 (Community 도 됨) | |
| Git | 아무 버전 | `git --version` |
| DBeaver | 최신 | 공유 DB 접속용 |
| (선택) MySQL 8 | 8.0 이상 | 내 컴퓨터 연습 DB 를 쓸 때만 |

> 공유 DB 서버는 **MySQL 8** 이다. SQL 파일의 `utf8mb4_0900_ai_ci` 는 MySQL 8 전용이라
> MariaDB 나 MySQL 5.7 로 연습 DB 를 만들면 이 부분에서 에러가 난다. 연습 DB 도 MySQL 8 로.

## 2. 프로젝트 받기

```bash
git clone <저장소 주소>
cd maze-lab
git checkout main && git pull
```

### 맥/리눅스만: gradlew 실행 권한
`./gradlew: Permission denied` 가 나오면 한 번만:
```bash
chmod +x gradlew
```
(저장소에도 실행 권한을 기록해 두었으니 새로 받으면 보통 안 나온다)

## 3. IntelliJ 설정 (한 번만)

| 메뉴 | 값 | 왜 |
|---|---|---|
| File → Project Structure → SDK | **21** | 팀 전원 같은 자바 |
| Settings → Build Tools → Gradle → Gradle JVM | **21** (Project SDK) | Gradle 도 같은 자바로 |
| Settings → Editor → File Encodings | 전부 **UTF-8** | 한글 주석 깨짐 방지 |
| Settings → Build → Compiler → Annotation Processors | **Enable annotation processing** 체크 | Lombok 이 동작하려면 필수 |
| Settings → Plugins | **Lombok** 설치 확인 | 빨간 줄 방지 |

> 줄바꿈(윈도우 CRLF / 맥 LF), 들여쓰기는 저장소의 `.gitattributes`, `.editorconfig` 가 자동으로 맞춘다. 손대지 않는다.

## 4. 실행 설정 (비밀번호는 여기에만)

`MazeLabApplication` ▶ 옆 ▼ → Edit Configurations → Environment variables:

```
DB_USERNAME=팀장에게 받은 아이디;DB_PASSWORD=팀장에게 받은 비번
```

- 아이디, 비번을 **파일에 쓰지 않는다.** `application.yml` 에는 `${DB_USERNAME}` 처럼 이름만 있다
- 8080 이 막혀 있으면 `;PORT=8081` 추가 (`application.yml` 은 고치지 않는다)

### (선택) 내 컴퓨터 연습 DB
공유 DB 에 실행하기 전에 SQL 을 먼저 시험해 보고 싶을 때.

1. 내 MySQL 8 에서 `CREATE DATABASE maze_local;`
2. `db/README.md` 의 "처음부터 DB 를 만드는 사람" 순서대로 SQL 실행
3. 실행 설정 → Active profiles: `local`, 환경 변수: `LOCAL_DB_USERNAME=root;LOCAL_DB_PASSWORD=내비번`

## 5. DBeaver 로 공유 DB 연결

| 칸 | 값 |
|---|---|
| Host | `1.220.247.76` |
| Port | `13306` |
| Database | `pr1_team1db` |
| 사용자 / 비번 | 실행 설정과 같은 값 |
| Driver properties | `allowPublicKeyRetrieval=true`, `useSSL=false` |

**규칙:** 공유 DB 에서 `DROP`, `TRUNCATE`, `WHERE` 없는 `DELETE/UPDATE` 금지. (`db/README.md`)

## 6. 확인

- [ ] `java -version` → 21
- [ ] Gradle 새로고침(🐘) 에러 없음
- [ ] `MazeLabApplication` 실행 → 콘솔에 `Started MazeLabApplication`
- [ ] http://localhost:8080 열림
- [ ] `src/test` 우클릭 → Run 'All Tests' → 전부 초록불
- [ ] DBeaver 에서 `SELECT COUNT(*) FROM member;` 실행됨

셋 다 체크가 끝나면 단톡에 "환경 OK" 한 줄.

## 자주 만나는 에러

| 에러 | 원인 | 해결 |
|---|---|---|
| `Schema-validation: missing table [xxx]` | Entity 는 있는데 공유 DB 에 테이블이 없음 | 테이블 주인이 SQL 실행했는지 확인. 실행 전이면 그 Entity 가 든 가지를 main 에 합치지 않는다 |
| `Schema-validation: wrong column type` | Entity 타입과 SQL 타입이 다름 | 짝꿍 SQL 의 "번역 메모" 확인 (`long` ↔ `BIGINT`, `boolean` ↔ `BOOLEAN`) |
| `Access denied for user` | 실행 설정 환경 변수 오타 | `DB_USERNAME`, `DB_PASSWORD` 철자 |
| `Unknown collation: utf8mb4_0900_ai_ci` | 연습 DB 가 MySQL 8 이 아님 | MySQL 8 로 |
| `./gradlew: Permission denied` | 실행 권한 없음 | `chmod +x gradlew` |
| Lombok `@Getter` 가 빨간 줄 | 어노테이션 처리 꺼짐 | 3번 표의 Annotation Processors 켜기 |
