/**
 * ===== [3번 랭킹·내 기록] 영토 =====   담당: ________
 *
 * 여기 만들 것:
 *   RankController.java     주소: /rank (랭킹), /me (내 기록·내 초파리 뇌)
 *
 * 2번 테이블은 "읽기만" 한다 (docs/DATA_CONTRACT.md B):
 *   랭킹  memberStatsRepository.findByTotalIsNotNullOrderByBrainGrowthDescTotalDesc(PageRequest.of(0, 20))
 *   등급  Grade.from(점수)   (점수가 null 이면 "측정 중")
 *   레벨  brainGrowth / 10
 *
 * 화면: templates/rank/
 */
package com.team.mazelab.rank;
