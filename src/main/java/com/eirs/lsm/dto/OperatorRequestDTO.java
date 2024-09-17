package com.eirs.lsm.dto;

import com.eirs.lsm.repository.entity.DeviceSyncOperation;
import com.eirs.lsm.repository.entity.DeviceSyncRequestListIdentity;
import com.eirs.lsm.repository.entity.DeviceSyncRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperatorRequestDTO {

    private Integer operation;

    private DeviceSyncOperation operationType;

    private String deviceId;

    private String imei;

    private String actualImei;

    private String imeiEsnMeid;

    private String imsi;

    private String msisdn;

    private String operatorId;

    private String operatorName;

    private Integer approveStatus;

    private LocalDateTime approvalDate;

    private String approvedBy;

    private LocalDateTime requestDate;

    private String tac;

    private Integer deleteFlag;

    private DeviceSyncRequestStatus status;

    private DeviceSyncRequestListIdentity listType;

}
