package com.eirs.lsm.dto;

import com.eirs.lsm.repository.entity.DeviceSyncOperation;
import com.eirs.lsm.repository.entity.DeviceSyncRequest;
import com.eirs.lsm.repository.entity.DeviceSyncRequestListIdentity;
import com.eirs.lsm.repository.entity.DeviceSyncRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSyncRequestList {

    List<DeviceSyncRequest> deviceSyncRequests;
}
