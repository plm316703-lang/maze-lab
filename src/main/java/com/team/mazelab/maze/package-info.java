/**
 * ===== [1번 미로 게임] 영토 =====   담당: ________
 *
 * 여기 만들 것:
 *   MazeController.java      화면 주소: /maze (스테이지 목록), /maze/{stageId} (미로 플레이)
 *
 * 게임 본체는 JavaScript:
 *   static/js/maze/          미로 생성기(씨앗 값 → 같은 미로), 이동, 타이머, FlyBot(BFS) 최단 경로
 *   templates/maze/          미로 화면 (CSRF meta 태그 필수)
 *
 * 2번과의 약속 (docs/DATA_CONTRACT.md A):
 *   GET  /api/stages                 스테이지 목록 받기 (mazeSize, mazeSeed, botMinTokens, targetSeconds)
 *   POST /api/plays                  한 판 끝나면 { stageId, moves, success, seconds } 보내기
 *   POST /api/stages/{id}/restart    처음부터 (하트 3개)
 *
 * ★ 같은 mazeSeed 면 언제나 같은 미로가 나와야 한다. 최단 이동 수가 DB 값과 다르면 2번에게 알린다.
 */
package com.team.mazelab.maze;
