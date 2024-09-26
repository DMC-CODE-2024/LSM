package com.eirs.lsm.alert;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Data
public class AlertConfig {

    @Value("${eirs.alert.url}")
    private String url;

    private Map<AlertIds, AlertConfigDto> alertsMapping;

    @PostConstruct
    public void init() {
        alertsMapping = new HashMap<>();
        alertsMapping.put(AlertIds.CONFIGURATION_VALUE_MISSING, new AlertConfigDto("alert2201"));
        alertsMapping.put(AlertIds.CONFIGURATION_VALUE_WRONG, new AlertConfigDto("alert2202"));
        alertsMapping.put(AlertIds.DATABASE_EXCEPTION, new AlertConfigDto("alert2206"));
        alertsMapping.put(AlertIds.LIST_SYNC_URL_EXCEPTION,new AlertConfigDto("alert2209"));
    }
}
