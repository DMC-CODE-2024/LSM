package com.eirs.lsm.repository;

import com.eirs.lsm.repository.entity.BlacklistDevice;
import com.eirs.lsm.repository.entity.GreylistDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Stream;


@Repository
public interface BlacklistDeviceHisRepository extends JpaRepository<BlacklistDevice, Long> {

    public Stream<BlacklistDevice> findByCreatedOnBetween(LocalDateTime startDate, LocalDateTime endDate);
}
