package com.team.mazelab.common;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 만든 시각, 고친 시각을 "자동으로" 채워 주는 부모 클래스.
 *
 * - @MappedSuperclass : 이 클래스 자체는 테이블이 아니다. 자식 Entity 의 테이블에 칸만 물려준다
 * - @EntityListeners  : 저장/수정 직전에 스프링이 끼어들어 시각을 넣는다
 * - 사용법 : public class GameCharacter extends BaseTimeEntity
 *            → 그 테이블에 created_at, updated_at 칸이 생긴다 (ddl-auto: update)
 *
 * EXTENSION: activity_log 처럼 "만든 시각만" 있는 테이블은 이 클래스를 상속하지 않고
 *            @CreatedDate 칸 하나만 직접 둔다
 */

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)

public abstract class BaseTimeEntity {

    @CreatedDate                                                      // 처음 save() 할 때 한 번 채워짐
    @Column(name = "created_at", nullable = false, updatable = false) // updatable=false : 이후 UPDATE 에서 빠짐
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime  updatedAt;

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
