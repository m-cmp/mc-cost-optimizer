package com.mcmp.cost.ncp.collector.repository;

import com.mcmp.cost.ncp.collector.entity.NcpCostVmMonth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NcpCostVmMonthRepository extends JpaRepository<NcpCostVmMonth, Long> {

    @Query(value = "SELECT * FROM ncp_cost_vm_month " +
            "WHERE DATE(write_date) = CURDATE() " +
            "AND member_no = :memberNo",
            nativeQuery = true)
    List<NcpCostVmMonth> findTodayDataByMemberNo(@Param("memberNo") String memberNo);
}
