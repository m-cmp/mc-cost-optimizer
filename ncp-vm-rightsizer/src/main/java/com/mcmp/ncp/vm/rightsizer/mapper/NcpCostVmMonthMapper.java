package com.mcmp.ncp.vm.rightsizer.mapper;

import com.mcmp.ncp.vm.rightsizer.dto.NcpCostVmMonthDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface NcpCostVmMonthMapper {

    List<NcpCostVmMonthDto> findLatestByMemberNo(@Param("memberNo") String memberNo);

    NcpCostVmMonthDto findLatestByMemberNoAndInstanceNo(
            @Param("memberNo") String memberNo,
            @Param("instanceNo") String instanceNo
    );

    List<NcpCostVmMonthDto> findVmListCurrentMonth();

    List<NcpCostVmMonthDto> selectVmListByMonth(@Param("demandMonth") String demandMonth);
}
