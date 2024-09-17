package com.eirs.lsm.service;

import com.eirs.lsm.dto.OperatorRequestDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ListService<T> {

    public List<OperatorRequestDTO> getIncremental(LocalDateTime startDate, LocalDateTime endDate);
}
