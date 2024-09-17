package com.eirs.lsm.service;

import com.eirs.lsm.dto.OperatorRequestDTO;
import com.eirs.lsm.mapper.DeviceSyncRequestMapper;
import com.eirs.lsm.repository.BlacklistDeviceHisRepository;
import com.eirs.lsm.repository.entity.BlacklistDevice;
import com.eirs.lsm.repository.entity.BlockedTac;
import com.eirs.lsm.repository.entity.DeviceSyncRequestListIdentity;
import com.eirs.lsm.repository.entity.DeviceSyncRequestStatus;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class BlacklistDeviceServiceImpl implements ListService<BlacklistDevice> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BlacklistDeviceHisRepository repository;

    private DeviceSyncRequestMapper mapper = Mappers.getMapper(DeviceSyncRequestMapper.class);

    @Transactional(readOnly = true)
    @Override
    public List<OperatorRequestDTO> getIncremental(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Reading for startDate:{} endDate:{}", startDate, endDate);
        List<OperatorRequestDTO> requests = null;
        try (Stream<BlacklistDevice> stream = repository.findByCreatedOnBetween(startDate, endDate)) {
            requests = toOperators(stream);
        }
        return requests;
    }

    private List<OperatorRequestDTO> toOperators(Stream<BlacklistDevice> list) {
        List<OperatorRequestDTO> requests = new ArrayList<>();
        list.forEach(data -> requests.add(mapToOperator(data)));
        return requests;
    }

    private OperatorRequestDTO mapToOperator(BlacklistDevice blacklistDevice) {
        OperatorRequestDTO dto = mapper.toOperatorRequestDTO(blacklistDevice);
        dto.setListType(DeviceSyncRequestListIdentity.BLOCKED_LIST);
        dto.setStatus(DeviceSyncRequestStatus.NEW);
        return dto;
    }
}
