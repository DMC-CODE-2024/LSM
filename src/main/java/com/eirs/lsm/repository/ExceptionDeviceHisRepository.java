package com.eirs.lsm.repository;

import com.eirs.lsm.repository.entity.ExceptionDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Stream;


@Repository
public interface ExceptionDeviceHisRepository extends JpaRepository<ExceptionDevice, Long> {

    public Stream<ExceptionDevice> findByCreatedOnBetween(LocalDateTime startDate, LocalDateTime endDate);
}
