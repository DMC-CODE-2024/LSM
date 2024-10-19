package com.eirs.lsm.service;

import com.eirs.lsm.alert.AlertIds;
import com.eirs.lsm.alert.AlertMessagePlaceholders;
import com.eirs.lsm.alert.AlertService;
import com.eirs.lsm.config.AppConfig;
import com.eirs.lsm.repository.entity.DeviceSyncRequestListIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ModuleAlertService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlertService alertService;

    @Autowired
    AppConfig appConfig;

    public void sendAlert(AlertIds alertIds, Map<AlertMessagePlaceholders, String> placeHolderMap) {
        placeHolderMap.put(AlertMessagePlaceholders.FEATURE_NAME, appConfig.getFeatureName());
        alertService.sendAlert(alertIds, placeHolderMap);
    }

    public void sendDatabaseAlert(String exception, DeviceSyncRequestListIdentity listIdentity) {
        Map<AlertMessagePlaceholders, String> map = new HashMap<>();
        map.put(AlertMessagePlaceholders.EXCEPTION, exception);
        map.put(AlertMessagePlaceholders.LIST, listIdentity.name());
        map.put(AlertMessagePlaceholders.FEATURE_NAME, appConfig.getFeatureName());
        alertService.sendAlert(AlertIds.DATABASE_EXCEPTION, map);
    }

    public void sendConfigurationMissingAlert(String configKey) {
        Map<AlertMessagePlaceholders, String> map = new HashMap<>();
        map.put(AlertMessagePlaceholders.CONFIG_KEY, configKey);
        map.put(AlertMessagePlaceholders.FEATURE_NAME, appConfig.getFeatureName());
        alertService.sendAlert(AlertIds.CONFIGURATION_VALUE_MISSING, map);
    }

    public void sendConfigurationWrongValueAlert(String configKey, String configValue) {
        Map<AlertMessagePlaceholders, String> map = new HashMap<>();
        map.put(AlertMessagePlaceholders.CONFIG_KEY, configKey);
        map.put(AlertMessagePlaceholders.CONFIG_VALUE, configValue);
        map.put(AlertMessagePlaceholders.FEATURE_NAME, appConfig.getFeatureName());
        alertService.sendAlert(AlertIds.CONFIGURATION_VALUE_WRONG, map);
    }

}
