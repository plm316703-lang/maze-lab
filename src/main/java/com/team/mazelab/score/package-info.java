/**
 * ===== [2번 게임 데이터] 영토 =====   담당: 박민혁
 *
 * 테이블 (db/02 ~ 07, 2번 첫 PR 에서 추가):
 *   brain_region, stage, play_attempt, stage_best, member_stats, member_region_progress
 *
 * 여기 만들 것 (아래에서 위로):
 *   Entity      BrainRegion, Stage, PlayAttempt, StageBest, MemberStats, MemberRegionProgress
 *   Repository  각 Entity 의 창고지기
 *   계산        Grade, AbilityScores, AbilityCalculator (최근 5판 → 능력치 4개 + 종합)
 *   Service     PlayService (한 판 저장 → 별 → 하트 → 영역 진행률 → 능력치, 하나의 트랜잭션)
 *               RegionProgressService (initFor: 가입할 때 영역마다 0% 줄)
 *   Controller  StageApiController, PlayApiController  (주소: /api/**, JSON)
 *
 * 1번과의 약속: docs/DATA_CONTRACT.md A  /  3번과의 약속: docs/DATA_CONTRACT.md B
 *
 * ★ Entity 를 main 에 합치기 직전에 db/02~07 을 공유 DB 에 실행한다 (validate).
 */
package com.team.mazelab.score;
