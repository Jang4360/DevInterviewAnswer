package dev.interview.server.global.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;

import java.time.LocalDateTime;

// 모든 Entity 에 공통으로 적용될 생성일 필드
@MappedSuperclass
@Getter
public class BaseEntity {
    @Column(name="created_at", nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
