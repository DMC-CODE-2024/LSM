package com.eirs.lsm.mapper;

import com.eirs.lsm.dto.OperatorRequestDTO;
import com.eirs.lsm.repository.entity.DeviceSyncOperation;
import com.eirs.lsm.repository.entity.DeviceSyncRequest;
import com.eirs.lsm.service.SystemConfigurationService;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class BeansMapper {

    private DeviceSyncRequestMapper mapper = Mappers.getMapper(DeviceSyncRequestMapper.class);

    @Autowired
    private SystemConfigurationService config;

    private List<DeviceSyncRequest> toOperatorRequest(OperatorRequestDTO dto, Integer noOfEirs) {
        List<DeviceSyncRequest> requests = new ArrayList<>();
        for (int eirId = 1; eirId <= noOfEirs; eirId++) {
            DeviceSyncRequest request = mapper.toDeviceSyncRequest(dto);
            request.setEirId(eirId);
            request.setInsertForSyncTime(LocalDateTime.now());
            request.setCreatedOn(LocalDateTime.now());
            request.setNoOfRetry(0);
            requests.add(request);
        }

        return requests;
    }

    public List<DeviceSyncRequest> toSingleOperatorRequest(OperatorRequestDTO dto) {
        return toOperatorRequest(dto, config.getNoOfEirs(dto.getOperatorName()));
    }

    public OperatorRequestDTO toOperatorRequestDTO(DeviceSyncRequest request) {
        OperatorRequestDTO requestDTO = mapper.toOperatorRequestDTO(request);
        requestDTO.setOperationType(DeviceSyncOperation.get(request.getOperation()));
        return requestDTO;
    }

    public List<DeviceSyncRequest> toOperatorRequest(OperatorRequestDTO dto) {
        List<DeviceSyncRequest> list = new ArrayList<>();
        config.getOperators().forEach(operator -> {
            dto.setOperatorName(operator);
            List<DeviceSyncRequest> requests = toOperatorRequest(dto, config.getNoOfEirs(operator));
            list.addAll(requests);
        });
        return list;
    }
}
