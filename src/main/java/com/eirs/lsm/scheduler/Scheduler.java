package com.eirs.lsm.scheduler;

import com.eirs.lsm.orchestration.DeviceSyncRequestOrchestration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class Scheduler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Value("${deviceSyncRequest.delete.beforeDays:5}")
    private Integer deleteBeforeDays;
    @Autowired
    private DeviceSyncRequestOrchestration deviceSyncRequestOrchestration;

    public void scheduler() {
        LocalDateTime startDate = LocalDateTime.now().minusMinutes(5);
        LocalDateTime endDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0));
        log.info("Daily Cronjob started at {} for startDate[{}] and endDate[{}]", LocalDateTime.now(), startDate, endDate);
    }

    @Scheduled(cron = "${scheduler.daily.cronjob:0 0 1 * * *}")
    public void hourlyScheduler() {
        LocalDate date = LocalDate.now().minusDays(deleteBeforeDays);
        log.info("Daily Cronjob started at {} for:{}", LocalDateTime.now(), date);
        deviceSyncRequestOrchestration.delete(date);
    }

}
