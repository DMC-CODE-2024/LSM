package com.eirs.lsm.service;

import com.eirs.lsm.repository.entity.DeviceSyncRequestListIdentity;
import com.eirs.lsm.repository.entity.DeviceSyncRequestPointer;

public interface DeviceSyncRequestPointerService {

    void updateLastProcessedDate(DeviceSyncRequestPointer deviceSyncRequestPointer);

    DeviceSyncRequestPointer getLastProcessedDate(DeviceSyncRequestListIdentity list_type);

}
