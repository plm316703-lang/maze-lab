package com.team.mazelab.activity;


import com.team.mazelab.character.GameCharacter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 활동 기록 창고지기.
 *
 * findTop100ByCharacterOrderByIdDesc
 *   → SELECT * FROM activity_log WHERE character_id = ? ORDER BY id DESC LIMIT 100
 *   Top100 : 최대 100줄만 (기록이 쌓여도 응답이 무거워지지 않게)
 *   id 역순 = 최신순 (id 는 저장 순서대로 커지니까)
 */
 public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findTop100ByCharacterOrderByIdDesc(GameCharacter character);

    // EXTENSION (통계): 종류별 합계 같은 집계는 @Query("SELECT SUM(a.oreDelta) ...") 로 추가한다}
}
