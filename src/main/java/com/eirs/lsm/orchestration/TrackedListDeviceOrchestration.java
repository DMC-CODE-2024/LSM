package com.eirs.lsm.orchestration;

import com.eirs.lsm.dto.OperatorRequestDTO;
import com.eirs.lsm.mapper.BeansMapper;
import com.eirs.lsm.repository.DeviceSyncRequestPointerRepository;
import com.eirs.lsm.repository.entity.*;
import com.eirs.lsm.service.*;
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
public class TrackedListDeviceOrchestration {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ListService<GreylistDevice> service;

    @Autowired
    private DeviceSyncRequestService operatorRequestService;


    @Autowired
    private DeviceSyncRequestPointerService deviceSyncRequestPointerService;

    @Autowired
    private SystemConfigurationService config;

    @Autowired
    private Validator validator;

    @Autowired
    private BeansMapper beansMapper;

    private boolean isAlreadyRunning = false;
    private String enableConfigKey = SystemConfigKeys.ENABLE_PROCESSING_TRACKED_LIST;

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
                        DeviceSyncRequestPointer deviceSyncRequestPointer = deviceSyncRequestPointerService.getLastProcessedDate(DeviceSyncRequestListIdentity.TRACKED_LIST);
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
                moduleAlertService.sendDatabaseAlert(e.getMessage(), DeviceSyncRequestListIdentity.TRACKED_LIST);
                log.error("Exception Error:{}", e.getMessage(), e);
            }
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("Tracked List reading is not in config with key:{} Expected Value: YES/TRUE", enableConfigKey);
    }

    private Boolean isEnabled() {
        String isEnable = config.findByKey(SystemConfigKeys.ENABLE_PROCESSING_TRACKED_LIST, "NO");
        if (StringUtils.equalsAnyIgnoreCase(isEnable, new String[]{"YES", "TRUE"})) {
            return true;
        }
        return false;
    }
}
