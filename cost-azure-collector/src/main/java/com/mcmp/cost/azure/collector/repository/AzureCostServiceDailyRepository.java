package com.mcmp.cost.azure.collector.repository;

import com.mcmp.cost.azure.collector.entity.AzureCostServiceDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AzureCostServiceDailyRepository extends JpaRepository<AzureCostServiceDaily, Long> {
}
