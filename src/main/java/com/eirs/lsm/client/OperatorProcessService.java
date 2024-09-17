package com.eirs.lsm.client;

import com.eirs.lsm.dto.OperatorResponseDTO;
import com.eirs.lsm.mapper.BeansMapper;
import com.eirs.lsm.repository.entity.DeviceSyncRequest;
import com.eirs.lsm.repository.entity.DeviceSyncRequestStatus;
import com.eirs.lsm.repository.entity.SystemConfigKeys;
import com.eirs.lsm.service.DeviceSyncRequestService;
import com.eirs.lsm.service.SystemConfigurationService;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Scope("prototype")
public class OperatorProcessService {
    public final Logger log = LoggerFactory.getLogger(this.getClass());

    private String operator;

    private Integer operatorEirId;

    private OperatorUrlDelegate operatorUrlService;

    @Autowired
    private DeviceSyncRequestService operatorRequestService;

//    @Autowired
//    private OperatorExecutor executor;

    @Autowired
    private SystemConfigurationService config;

    @Autowired
    private BeansMapper beansMapper;

    RateLimiter rateLimiter;

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setOperatorEirId(Integer operatorEirId) {
        this.operatorEirId = operatorEirId;
    }

    public void setOperatorUrlService(OperatorUrlDelegate operatorUrlService) {
        this.operatorUrlService = operatorUrlService;
    }

    public void init() {
        new Thread(() -> consume()).start();
    }

    private void consume() {
        String tpsKey = SystemConfigKeys.OPERATOR_TPS.replaceAll("<OPERATOR>", operator);
        Integer tps = config.findByKey(tpsKey, SystemConfigKeys.DEFAULT_OPERATOR_TPS);
        rateLimiter = RateLimiter.create(tps);
//        log.info("Operator:{}, Process:{} Executor:{} TPS-key:{},TPS-value:{}", operator, this, executor, tpsKey, tps);
        log.info("Process started Operator:{} , operatorEirId:{} Process:{} TPS-key:{},TPS-value:{} ", operator, operatorEirId, this, tpsKey, tps);
        while (true) {
            try {
                List<DeviceSyncRequest> requests = operatorRequestService.getRetryRequest(operator, operatorEirId);
                if (CollectionUtils.isEmpty(requests)) {
                    requests = operatorRequestService.getNewRequests(operator, operatorEirId);
                }
                if (CollectionUtils.isEmpty(requests))
                    TimeUnit.SECONDS.sleep(10);
                execute(requests);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void execute(List<DeviceSyncRequest> requests) {
        try {
            if (!CollectionUtils.isEmpty(requests)) {
                for (DeviceSyncRequest request : requests) {
                    rateLimiter.acquire();
                    process(request);
                    if (request.getStatus() == DeviceSyncRequestStatus.CONNECTION_FAILED || request.getStatus() == DeviceSyncRequestStatus.FAILED) {
                        Integer waitInMinutes = config.findByKey(SystemConfigKeys.FAILED_SYNC_RETRY_AFTER_MINUTES, 5);
                        TimeUnit.MINUTES.sleep(waitInMinutes);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Exception Error:{}", e.getMessage(), e);
        }
    }

    private void process(DeviceSyncRequest request) {
        if (request.getStatus() == DeviceSyncRequestStatus.FAILED || request.getStatus() == DeviceSyncRequestStatus.CONNECTION_FAILED)
            request.setNoOfRetry(request.getNoOfRetry() + 1);

        request.setStatus(DeviceSyncRequestStatus.INIT);

        operatorRequestService.save(request);
        request.setSyncRequestTime(LocalDateTime.now());
        OperatorResponseDTO responseDTO = operatorUrlService.callUrl(beansMapper.toOperatorRequestDTO(request));
        request.setSyncResponseTime(responseDTO.getResposeTime());
        request.setFailureReason(responseDTO.getFailureReason());
        request.setStatus(responseDTO.getStatus());
        operatorRequestService.save(request);
    }

}
