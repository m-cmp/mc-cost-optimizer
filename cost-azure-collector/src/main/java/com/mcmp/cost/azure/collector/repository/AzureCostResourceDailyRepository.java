package com.mcmp.cost.azure.collector.repository;

import com.mcmp.cost.azure.collector.entity.AzureCostResourceDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Collection;

@Repository
public interface AzureCostResourceDailyRepository extends JpaRepository<AzureCostResourceDaily, Long> {

    /**
     * 재수집 멱등성: 같은 subscription 의 같은 usage_date 행을 먼저 지우고 다시 넣는다.
     * (writer 의 chunk 트랜잭션 안에서 실행되어 DELETE→INSERT 가 원자적)
     * azure_cost_vm_daily 와 달리 UNIQUE(subscription_id, resource_id, usage_date) 가 있어 중복 적재가 불가능하다.
     */
    @Modifying
    @Query("DELETE FROM AzureCostResourceDaily r WHERE r.subscriptionId = :subscriptionId AND r.usageDate IN :usageDates")
    int deleteBySubscriptionIdAndUsageDates(@Param("subscriptionId") String subscriptionId,
                                            @Param("usageDates") Collection<String> usageDates);
}
