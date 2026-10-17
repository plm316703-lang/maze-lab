package com.team.mazelab.enhance;


import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.team.mazelab.item.Grade.*;

/**
 * 서버가 켜질 때 강화표 4줄을 DB 에 맞춘다. (ItemCatalogInitializer 와 같은 방식)
 *   없으면 INSERT, 있으면 syncFrom 으로 값 맞추기
 *
 * ★ 밸런스를 바꾸고 싶으면 아래 숫자만 고치고 서버를 다시 켜면 된다
 *   (값은 프론트 프로토타입 v30 ENHANCE_RULES 와 같다)
 */
@Component
public class EnhanceRuleInitializer  implements  CommandLineRunner{

    private static final long SHINING_GEM  = 5001L;  //빛나는 원석(광산)
    private static final long DEEP_PEARL = 5002L;   //심해 진주(낚시)

    static final List<EnhanceRule> RULES = List.of(
        //                         현재, 다음, 확률,  광석, 골드
        EnhanceRule.basic(         D,    C,    0.90,  5,    50),
        EnhanceRule.basic(         C,    B,    0.70,  15,   150),
        //                         현재, 다음, 확률,  광석, 골드,  재료,        개수, 실패 시
        EnhanceRule.withMaterial(  B,    A,    0.45,  40,   400,   SHINING_GEM, 1,    null),
        EnhanceRule.withMaterial(  A,    S,    0.20,  100,  1000,  DEEP_PEARL,  1,    B)
    );
    private final EnhanceRuleRepository ruleRepository;

    public EnhanceRuleInitializer(EnhanceRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }
    @Override
    @Transactional
    public void run(String... args) {
        for (EnhanceRule source : RULES) {
            ruleRepository.findById(source.getFromGrade()).ifPresentOrElse(
                saved -> saved.syncFrom(source),      // 있으면 값 맞추기 (UPDATE)
                () -> ruleRepository.save(source)     // 없으면 넣기 (INSERT)
            );
        }
    }
}
