package com.team.mazelab.enhance;


import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 강화 주사위. 0.0 이상 1.0 미만의 수를 하나 굴린다.
 *   굴린 값 < 성공률 이면 성공   (예: 0.37 < 0.45 → B→A 성공)
 *
 * ★ 왜 Math.random() 을 Service 안에 바로 쓰지 않고 따로 뺐나?
 *   테스트에서 "무조건 성공", "무조건 실패" 를 만들어야 하기 때문이다.
 *   이렇게 Bean 으로 빼 두면 테스트가 이 Bean 을 가짜(Mock)로 바꿔 끼워서
 *   roll() 이 0.0(성공) 이나 0.99(실패) 를 돌려주게 만들 수 있다.
 *   → 확률 기능도 "매번 같은 결과" 로 검사할 수 있다
 *
 * ThreadLocalRandom : 여러 요청이 동시에 와도 서로 기다리지 않는 난수 도구
 */
@Component
public class EnhanceRoller {
    public double roll() {
        return ThreadLocalRandom.current().nextDouble();
    }
}
