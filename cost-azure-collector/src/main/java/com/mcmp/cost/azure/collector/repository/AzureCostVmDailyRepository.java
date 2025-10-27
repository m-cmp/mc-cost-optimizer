package com.mcmp.cost.azure.collector.repository;

import com.mcmp.cost.azure.collector.entity.AzureCostVmDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AzureCostVmDailyRepository extends JpaRepository<AzureCostVmDaily, Long> {

    @Query(value = "SELECT * FROM azure_cost_vm_daily " +
            "WHERE subscription_id = :subscriptionId " +
            "AND DATE_FORMAT(usage_date, '%Y-%m') = :yearMonth",
            nativeQuery = true)
    List<AzureCostVmDaily> findBySubscriptionIdAndYearMonth(
            @Param("subscriptionId") String subscriptionId,
            @Param("yearMonth") String yearMonth
    );
}
