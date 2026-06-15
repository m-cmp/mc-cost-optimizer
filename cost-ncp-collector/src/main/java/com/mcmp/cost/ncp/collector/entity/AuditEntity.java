package com.mcmp.cost.ncp.collector.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
@EntityListeners(value = {AuditingEntityListener.class})
public class AuditEntity {

    /**
     * created.
     */
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime created;

    /**
     * updated.
     */
    @LastModifiedDate
    private LocalDateTime updated;
}
