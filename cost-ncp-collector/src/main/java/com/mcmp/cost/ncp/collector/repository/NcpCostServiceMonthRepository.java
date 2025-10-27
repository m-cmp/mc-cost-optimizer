package com.mcmp.cost.ncp.collector.repository;

import com.mcmp.cost.ncp.collector.entity.NcpCostServiceMonth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NcpCostServiceMonthRepository extends JpaRepository<NcpCostServiceMonth, Long> {
}
