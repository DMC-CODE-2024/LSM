package com.eirs.lsm.service;

import com.eirs.lsm.repository.DeviceSyncRequestPointerRepository;
import com.eirs.lsm.repository.entity.DeviceSyncRequestListIdentity;
import com.eirs.lsm.repository.entity.DeviceSyncRequestPointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class DeviceSyncRequestPointerServiceImpl implements DeviceSyncRequestPointerService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DeviceSyncRequestPointerRepository requestPointerRepository;

    public void updateLastProcessedDate(DeviceSyncRequestPointer deviceSyncRequestPointer) {
        DeviceSyncRequestPointer syncRequestPointer = requestPointerRepository.save(deviceSyncRequestPointer);
        log.info("DeviceSyncRequestPointer saved {}", syncRequestPointer);
    }

    public DeviceSyncRequestPointer getLastProcessedDate(DeviceSyncRequestListIdentity list_type) {
        DeviceSyncRequestPointer syncRequestPointer = requestPointerRepository.findLastTillDate(list_type);
        if (syncRequestPointer == null) {
            syncRequestPointer = DeviceSyncRequestPointer.builder()
                    .syncedTillDate(LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(0, 0, 0)))
                    .createdOn(LocalDateTime.now())
                    .listType(list_type).build();
        }
        return syncRequestPointer;
    }

}
