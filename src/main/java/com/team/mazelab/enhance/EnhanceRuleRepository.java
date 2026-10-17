package com.team.mazelab.enhance;


import com.team.mazelab.item.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 강화 규칙 창고지기.
 * JpaRepository<EnhanceRule, Grade> : PK 의 타입이 Long 이 아니라 Grade 다
 *   → findById(Grade.D) 로 "D 의 규칙" 을 찾는다
 */
public interface EnhanceRuleRepository extends JpaRepository<EnhanceRule, Grade> {


}
