package com.eirs.lsm.service;

import com.eirs.lsm.repository.entity.DeviceSyncRequest;

import java.util.List;

public interface DeviceSyncRequestService {

    DeviceSyncRequest save(DeviceSyncRequest request);

    void saveAll(List<DeviceSyncRequest> requests);

    List<DeviceSyncRequest> getNewRequests(String operator, Integer eirId);

    List<DeviceSyncRequest> getRetryRequest(String operator, Integer eirId);

    DeviceSyncRequest delete(DeviceSyncRequest request);

}
