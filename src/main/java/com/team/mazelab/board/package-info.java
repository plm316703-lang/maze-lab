/**
 * ===== [3번 게시판] 영토 =====   담당: ________
 *
 * 독서관 게시판 코드는 가져오지 않았다. 처음부터 새로 만든다.
 *
 * 순서: Board(Entity) → BoardRepository → BoardForm → BoardService → BoardController → 화면
 *   주소: /boards (목록), /boards/{id} (상세), /boards/new (쓰기)
 *   테이블: db/08_board.sql 을 새로 만든다 (번호 08 은 3번 몫으로 비워 둠)
 *
 * 회원 연결:
 *   @ManyToOne(fetch = FetchType.LAZY, optional = false)
 *   @JoinColumn(name = "member_id")
 *   private Member member;
 *
 * 화면: templates/board/
 */
package com.team.mazelab.board;
