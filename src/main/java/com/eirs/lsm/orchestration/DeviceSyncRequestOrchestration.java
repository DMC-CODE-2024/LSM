package com.eirs.lsm.orchestration;

import com.eirs.lsm.repository.DeviceSyncRequestRepository;
import com.eirs.lsm.repository.entity.DeviceSyncRequestStatus;
import com.eirs.lsm.writer.DeviceSyncRequestWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class DeviceSyncRequestOrchestration {

    private final Logger log = LogManager.getLogger(this.getClass());

    @Autowired
    DeviceSyncRequestRepository deviceSyncRequestRepository;

    @Autowired
    DeviceSyncRequestWriter deviceSyncRequestWriter;

    public void delete(LocalDate date) {
        LocalDateTime beforeDate = LocalDateTime.of(date.plusDays(1), LocalTime.of(0, 0));
        deviceSyncRequestWriter.writeFullData(beforeDate);
        Integer result = deviceSyncRequestRepository.deleteByRequestDateLessThanAndStatus(beforeDate, DeviceSyncRequestStatus.SYNCED);
        log.info("Deleted all the entries from DeviceSyncRequest Entity before date:{} result:{}", beforeDate, result);
    }

}
