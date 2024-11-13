package com.eirs.lsm.service;

import com.eirs.lsm.dto.DeviceSyncRequestList;
import com.eirs.lsm.dto.OperatorRequestDTO;
import com.eirs.lsm.mapper.BeansMapper;
import com.eirs.lsm.mapper.DeviceSyncRequestMapper;
import com.eirs.lsm.repository.ExceptionDeviceHisRepository;
import com.eirs.lsm.repository.entity.*;
import com.eirs.lsm.validator.Validator;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Service
public class ExceptionDeviceServiceImpl implements ListService<ExceptionDevice> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ExceptionDeviceHisRepository repository;

    @Autowired
    private DeviceSyncRequestService operatorRequestService;

    @Autowired
    private BeansMapper beansMapper;

    @Autowired
    private SystemConfigurationService config;

    @Autowired
    private Validator validator;

    private DeviceSyncRequestMapper mapper = Mappers.getMapper(DeviceSyncRequestMapper.class);

    @Transactional(readOnly = true)
    @Override
    public void sync(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Going to Sync for startDate:{} endDate:{}", startDate, endDate);
        try (Stream<ExceptionDevice> stream = repository.findByCreatedOnBetween(startDate, endDate)) {
            toOperators(stream);
        }
    }

    private void toOperators(Stream<ExceptionDevice> list) {
        DeviceSyncRequestList deviceSyncRequestList = new DeviceSyncRequestList(new ArrayList<>());
        list.forEach(data -> {
            deviceSyncRequestList.getDeviceSyncRequests().addAll(getOperatorRequests(data));
            if (deviceSyncRequestList.getDeviceSyncRequests().size() > 5000) {
                log.info("Going to save GreylistDevice Batch to Device of Size:{}", deviceSyncRequestList.getDeviceSyncRequests().size());
                try {
                    CompletableFuture.runAsync(() -> operatorRequestService.saveAll(deviceSyncRequestList.getDeviceSyncRequests())).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                operatorRequestService.saveAll(deviceSyncRequestList.getDeviceSyncRequests());
                deviceSyncRequestList.setDeviceSyncRequests(new ArrayList<>());
            }
        });
        if (!CollectionUtils.isEmpty(deviceSyncRequestList.getDeviceSyncRequests())) {
            log.info("Going to save Blacklist Batch to Device of Size:{}", deviceSyncRequestList.getDeviceSyncRequests().size());
            try {
                CompletableFuture.runAsync(() -> operatorRequestService.saveAll(deviceSyncRequestList.getDeviceSyncRequests())).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<DeviceSyncRequest> getOperatorRequests(ExceptionDevice device) {
        List<DeviceSyncRequest> requests = new ArrayList<>();
        if (validator.isOperatorRequest(device.getOperatorName())) {
            log.info("Operator Converting to OperatorRequestDTO:{}", device);
            if (config.getOperators().contains(device.getOperatorName().toUpperCase()))
                requests.addAll(beansMapper.toSingleOperatorRequest(mapToOperator(device)));
            else
                log.info("Skipping this record as Operator is not enabled OperatorRequestDTO:{}", device);
        } else {
            log.info("All Converting to OperatorRequestDTO:{}", device);
            requests.addAll(beansMapper.toOperatorRequest(mapToOperator(device)));
        }
        return requests;
    }

    private OperatorRequestDTO mapToOperator(ExceptionDevice exceptionDevice) {
        OperatorRequestDTO dto = mapper.toOperatorRequestDTO(exceptionDevice);
        dto.setListType(DeviceSyncRequestListIdentity.EXCEPTION_LIST);
        dto.setStatus(DeviceSyncRequestStatus.NEW);
        return dto;
    }
}
