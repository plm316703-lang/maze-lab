package com.team.mazelab.enhance;

import com.team.mazelab.item.Grade;

/**
 * GET /api/enhance-rules 의 한 줄. 프론트가 "강화 확률표" 화면을 그린다.
 *   {"fromGrade":"B","toGrade":"A","successRate":0.45,"oreCost":40,"goldCost":400,
 *    "materialItemId":5001,"materialCount":1,"failGrade":null}
 */
public record EnhanceRuleResponse(Grade fromGrade, Grade toGrade, double successRate,
                                  int oreCost, int goldCost,
                                  Long materialItemId, int materialCount, Grade failGrade) {

    public static EnhanceRuleResponse from(EnhanceRule r) {
        return new EnhanceRuleResponse(r.getFromGrade(), r.getToGrade(), r.getSuccessRate(),
            r.getOreCost(), r.getGoldCost(), r.getMaterialItemId(), r.getMaterialCount(), r.getFailGrade());
    }
}
