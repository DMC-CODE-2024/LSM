package com.eirs.lsm.service;

import com.eirs.lsm.dto.OperatorRequestDTO;
import com.eirs.lsm.mapper.DeviceSyncRequestMapper;
import com.eirs.lsm.repository.BlockTacHisRepository;
import com.eirs.lsm.repository.entity.BlockedTac;
import com.eirs.lsm.repository.entity.DeviceSyncRequestListIdentity;
import com.eirs.lsm.repository.entity.DeviceSyncRequestStatus;
import org.apache.commons.lang3.StringUtils;
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
public class BlockedTacServiceImpl implements ListService<BlockedTac> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BlockTacHisRepository repository;

    private DeviceSyncRequestMapper mapper = Mappers.getMapper(DeviceSyncRequestMapper.class);

    @Transactional(readOnly = true)
    @Override
    public List<OperatorRequestDTO> getIncremental(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Reading for startDate:{} endDate:{}", startDate, endDate);
        List<OperatorRequestDTO> requests = null;
        try (Stream<BlockedTac> stream = repository.findByCreatedOnBetween(startDate, endDate)) {
            requests = toOperators(stream);
        }
        return requests;
    }

    private List<OperatorRequestDTO> toOperators(Stream<BlockedTac> list) {
        List<OperatorRequestDTO> requests = new ArrayList<>();
        list.forEach(data -> {
            if (StringUtils.isBlank(data.getTac())) {
                log.info("Ignored request {}", data);
            } else {
                OperatorRequestDTO requestDTO = mapToOperator(data);
                requests.add(requestDTO);
            }
        });
        return requests;
    }

    private OperatorRequestDTO mapToOperator(BlockedTac blockedTac) {
        OperatorRequestDTO dto = mapper.toOperatorRequestDTO(blockedTac);
        dto.setListType(DeviceSyncRequestListIdentity.BLOCKED_TAC);
        dto.setStatus(DeviceSyncRequestStatus.NEW);
        return dto;
    }
}
