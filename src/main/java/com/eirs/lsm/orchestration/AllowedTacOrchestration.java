package com.eirs.lsm.orchestration;

import com.eirs.lsm.dto.OperatorRequestDTO;
import com.eirs.lsm.mapper.BeansMapper;
import com.eirs.lsm.repository.DeviceSyncRequestPointerRepository;
import com.eirs.lsm.repository.entity.*;
import com.eirs.lsm.service.*;
import com.eirs.lsm.utils.DateFormatterConstants;
import com.eirs.lsm.validator.Validator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class AllowedTacOrchestration {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ListService<AllowedTac> service;

    @Autowired
    private DeviceSyncRequestService operatorRequestService;


    @Autowired
    private DeviceSyncRequestPointerService deviceSyncRequestPointerService;

    @Autowired
    private SystemConfigurationService config;

    @Autowired
    private Validator validator;

    private boolean isAlreadyRunning = false;

    @Autowired
    private BeansMapper beansMapper;

    private String enableConfigKey = SystemConfigKeys.ENABLE_PROCESSING_ALLOWED_TAC;

    public void init() {
        new Thread(this::startProcessing).start();
    }

    @Autowired
    private DeviceSyncRequestPointerRepository deviceSyncRequestPointerRepository;
    @Autowired
    private ModuleAlertService moduleAlertService;

    public void startProcessing() {
        Integer pickTimeBeforeInMinutes = config.findByKey(SystemConfigKeys.PICK_DATE_BEFORE_TIME, SystemConfigKeys.DEFAULT_PICK_DATE_BEFORE_TIME);
        while (isEnabled()) {
            try {
                while (!isAlreadyRunning) {
                    int timeToRun = LocalTime.now().getMinute() % pickTimeBeforeInMinutes;
                    log.info("timeToRun:{}", (pickTimeBeforeInMinutes - timeToRun));
                    if (timeToRun == 0) {
                        LocalDateTime now = LocalDateTime.now().minusMinutes(pickTimeBeforeInMinutes + pickTimeBeforeInMinutes).withSecond(0).withNano(0);
                        DeviceSyncRequestPointer deviceSyncRequestPointer = deviceSyncRequestPointerService.getLastProcessedDate(DeviceSyncRequestListIdentity.ALLOWED_TAC);
                        LocalDateTime lastProcessedDate = deviceSyncRequestPointer.getSyncedTillDate();
                        if (now.isAfter(lastProcessedDate)) {
                            isAlreadyRunning = true;
                            LocalDateTime startDate = lastProcessedDate;
                            LocalDateTime endDate = LocalDateTime.now().minusMinutes(pickTimeBeforeInMinutes).withSecond(0).withNano(0);
                            service.sync(startDate, endDate);
                            deviceSyncRequestPointer.setSyncedTillDate(endDate);
                            deviceSyncRequestPointerService.updateLastProcessedDate(deviceSyncRequestPointer);
                            isAlreadyRunning = false;
                        }
                    }
                    TimeUnit.SECONDS.sleep(15);
                }
            } catch (Exception e) {
                log.error("Exception Error:{}", e.getMessage(), e);
                moduleAlertService.sendDatabaseAlert(e.getMessage(), DeviceSyncRequestListIdentity.ALLOWED_TAC);
            }
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("AllowedTac List reading is not in config with key:{} Expected Value: YES/TRUE", enableConfigKey);
    }

    private Boolean isEnabled() {
        String isEnable = config.findByKey(enableConfigKey, "NO");
        if (StringUtils.equalsAnyIgnoreCase(isEnable, new String[]{"YES", "TRUE"})) {
            return true;
        }
        return false;
    }
}
