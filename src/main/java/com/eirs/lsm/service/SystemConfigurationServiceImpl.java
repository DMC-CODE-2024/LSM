package com.eirs.lsm.service;

import com.eirs.lsm.config.AppConfig;
import com.eirs.lsm.repository.ConfigRepository;
import com.eirs.lsm.repository.entity.SysParam;
import com.eirs.lsm.repository.entity.SystemConfigKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class SystemConfigurationServiceImpl implements SystemConfigurationService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ConfigRepository repository;

    List<String> operators = new ArrayList<>();

    Map<String, Integer> operatorsEirs = new HashMap<>();
    @Autowired
    AppConfig appConfig;

    @Autowired
    private ModuleAlertService moduleAlertService;

    @Override
    public synchronized List<String> getOperators() {
        if (CollectionUtils.isEmpty(operators)) {
            Integer noOfOperators = findByKey(SystemConfigKeys.LSM_NO_OF_OPERATORS, 0);
            for (int i = 1; i <= noOfOperators; i++) {
                String operator = findByKey(SystemConfigKeys.LSM_OPERATOR.replaceAll("<NUMBER>", String.valueOf(i)));
                Integer noOfEirs = findByKey(SystemConfigKeys.OPERATOR_NO_OF_EIRS.replaceAll("<OPERATOR>", operator), 1);
                operators.add(operator.toUpperCase());
                log.info("NoOfEirs:{} for Operator:{}", noOfEirs, operator);
                operatorsEirs.put(operator.toUpperCase(), noOfEirs);
            }
        }
        return operators;
    }

    public String findByKey(String key) throws RuntimeException {
        try {
            Optional<SysParam> optional = repository.findByConfigKeyIgnoreCaseAndModule(key, appConfig.getModuleName());
            if (optional.isPresent()) {
                log.info("Filled key:{} value:{}", key, optional.get().getConfigValue());
                return optional.get().getConfigValue();
            } else {
                log.info("Value for key:{} Not Found", key);
                moduleAlertService.sendConfigurationMissingAlert(key);
                throw new RuntimeException("Config Key:" + key + ", value not found");
            }
        } catch (Exception e) {
            log.error("Error while finding Key:{} Error:{}", key, e.getMessage(), e);
            throw new RuntimeException("Config Key:" + key + ", value not found");
        }
    }


    public Integer findByKey(String key, int defaultValue) {
        String value = null;
        try {
            value = findByKey(key);
            try {
                return Integer.parseInt(value);
            } catch (RuntimeException e) {
                moduleAlertService.sendConfigurationWrongValueAlert(key, value);
                return defaultValue;
            }
        } catch (RuntimeException e) {
            return defaultValue;
        }

    }

    public String findByKey(String key, String defaultValue) {
        try {
            return findByKey(key);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    public Float findByKey(String key, float defaultValue) {
        String value = null;
        try {
            value = findByKey(key);
            try {
                return Float.parseFloat(value);
            } catch (RuntimeException e) {
                moduleAlertService.sendConfigurationWrongValueAlert(key, value);
                return defaultValue;
            }
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    public Integer getNoOfEirs(String operator) {
        Integer value = operatorsEirs.get(operator.toUpperCase());
        if (value == null) {
            log.error("Please check No of Eirs not configured for operator:{}", operator);
            return 0;
        }
        return value;
    }
}
