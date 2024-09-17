package com.eirs.lsm.service;

import com.eirs.lsm.repository.DeviceSyncRequestRepository;
import com.eirs.lsm.repository.entity.DeviceSyncRequest;
import com.eirs.lsm.repository.entity.DeviceSyncRequestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DeviceSyncRequestServiceImpl implements DeviceSyncRequestService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public DeviceSyncRequestRepository repository;

    @Override
    public DeviceSyncRequest save(DeviceSyncRequest request) {
        return repository.save(request);
    }

    @Override
    public void saveAll(List<DeviceSyncRequest> requests) {
        repository.saveAll(requests);
    }

    @Override
    public List<DeviceSyncRequest> getNewRequests(String operator, Integer eirId) {
        return repository.findRequests(operator, eirId, Collections.singletonList(DeviceSyncRequestStatus.NEW));
    }

    @Override
    public List<DeviceSyncRequest> getRetryRequest(String operator, Integer eirId) {
        List<DeviceSyncRequestStatus> statuses = new ArrayList<>();
        statuses.add(DeviceSyncRequestStatus.FAILED);
        statuses.add(DeviceSyncRequestStatus.CONNECTION_FAILED);
        return repository.findRequests(operator, eirId, statuses);
    }

    @Override
    public DeviceSyncRequest delete(DeviceSyncRequest request) {
        return null;
    }
}
