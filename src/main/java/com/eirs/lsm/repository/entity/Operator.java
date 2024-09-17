package com.eirs.lsm.repository.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Operator {

    private String imsi;

    private String operatorName;

    private String imei;

    private String tac;

    private String msisdn;

}
