package com.eirs.lsm.dto;

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
public class OperatorResponseDTO {

    private String operation;

    private String tac;

    private String imei;

    private String imsi;

    private String msisdn;

    private String operator;

    private DeviceSyncRequestStatus status;

    private String failureReason;

    private DeviceSyncRequestListIdentity identity;

    private LocalDateTime resposeTime;

    private Integer retryCount = 0;
}
