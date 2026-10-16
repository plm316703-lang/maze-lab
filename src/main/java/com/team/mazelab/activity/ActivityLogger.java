package com.team.mazelab.activity;


import com.team.mazelab.character.GameCharacter;
import com.team.mazelab.item.Item;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 활동 기록 장치. 다른 기능은 이 클래스의 log(...) 하나만 알면 된다 (팀과의 약속).
 *
 *   activityLogger.log(character, ActivityType.SHOP_BUY, Delta.gold(-50), item, "나무 검 [D] 구매");
 *
 * ★ Propagation.MANDATORY : "반드시 이미 진행 중인 트랜잭션 안에서만" 실행된다.
 *   - 기록은 그 활동(골드 차감, 등급 변경)과 "한 묶음"이어야 한다
 *     → 활동이 실패해서 취소되면 기록도 같이 취소된다
 *   - 트랜잭션 밖(예: @Transactional 이 없는 메서드)에서 부르면
 *     IllegalTransactionStateException 이 바로 터진다 → 실수를 개발 중에 알아챈다
 */
@Component
public class ActivityLogger {

   private final ActivityLogRepository activityLogRepository;

    public ActivityLogger(ActivityLogRepository activityLogRepository){
        this.activityLogRepository = activityLogRepository;
    }

    //아이쳄이 관련된 기록
    @Transactional(propagation = Propagation.MANDATORY)
    public ActivityLog log(GameCharacter character, ActivityType type, Delta delta, Item item, String detail) {
        return activityLogRepository.save(ActivityLog.of(character, type, delta, item, detail));
    }

    /** 아이템이 없는 기록 (재화만 움직임) */
    @Transactional(propagation = Propagation.MANDATORY)
    public ActivityLog log(GameCharacter character, ActivityType type, Delta delta, String detail) {
        return log(character, type, delta, null, detail);
    }
}
