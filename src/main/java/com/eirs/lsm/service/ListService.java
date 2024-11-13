package com.eirs.lsm.service;

import com.eirs.lsm.dto.OperatorRequestDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ListService<T> {

    void sync(LocalDateTime startDate, LocalDateTime endDate);
}
