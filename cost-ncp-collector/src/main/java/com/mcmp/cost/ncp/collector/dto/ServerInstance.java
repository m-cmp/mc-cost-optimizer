package com.mcmp.cost.ncp.collector.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import java.util.List;

/**
 * 서버 인스턴스
 * <a href="https://api.ncloud-docs.com/docs/common-vapidatatype-serverinstance">ServerInstance</a>
 */
@Getter
@Setter
@NoArgsConstructor
public class ServerInstance {

    /**
     * 서버 인스턴스 번호 ex) 16880170
     */
    private String serverInstanceNo;

    /**
     * 서버 이름
     */
    private String serverName;

    /**
     * 서버 설명
     */
    private String serverDescription;

    /**
     * 	Virtual CPU 개수
     */
    private Integer cpuCount;

    /**
     * 	메모리 사이즈(Byte)
     */
    private Long memorySize;

    /**
     * 서버에 설치된 OS 이미지의 플랫폼 구분
     */
    private CommonCode platformType;
    private String loginKeyName;
    private String publicIpInstanceNo;
    private String publicIp;
    private CommonCode serverInstanceStatus;
    private CommonCode serverInstanceOperation;
    private String serverInstanceStatusName;
    private Date createDate;
    private Date uptime;
    private String serverImageProductCode;
    private String serverProductCode;
    private Boolean isProtectServerTermination;
    private String zoneCode;

    /**
     * 서버가 위치한 리전 코드 ex) KR
     */
    private String regionCode;
    private String vpcNo;
    private String subnetNo;
    //    private NetworkInterfaceNoList networkInterfaceNoList;
    private String initScriptNo;
    private CommonCode serverInstanceType;
    private CommonCode baseBlockStorageDiskType;
    private CommonCode baseBlockStorageDiskDetailType;
    private String placementGroupNo;
    private String placementGroupName;
    private String memberServerImageInstanceNo;
    //    private List<BlockDevicePartition> blockDevicePartitionList;
    private CommonCode hypervisorType;
    private String serverImageNo;

    /**
     * 	서버 스펙 코드 ex) s2-g2-s50
     */
    private String serverSpecCode;
    private List<String> eventList;
    private Long fabricClusterPoolNo;
    private String fabricClusterPoolName;
    private String fabricClusterMode;
    private Long fabricClusterNo;
    private String fabricClusterName;
    private Boolean isPreInstallGpuDriver;
}
