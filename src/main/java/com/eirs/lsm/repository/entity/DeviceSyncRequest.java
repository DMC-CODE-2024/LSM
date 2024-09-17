package com.eirs.lsm.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceSyncRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imei;

    private String msisdn;

    private String imsi;

    private String actualImei;

    private String operatorName;

    private Integer eirId;

    private String tac;

    @Enumerated(EnumType.STRING)
    private DeviceSyncRequestStatus status;

    @Enumerated(EnumType.STRING)
    private DeviceSyncRequestListIdentity listType;

    private String failureReason;


    ///////////////

    private LocalDateTime requestDate;

    private Integer operation;

    private LocalDateTime createdOn;

    private LocalDateTime insertForSyncTime;

    private LocalDateTime syncRequestTime;

    private LocalDateTime syncResponseTime;

    private Integer noOfRetry;


}
