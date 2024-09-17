package com.eirs.lsm.repository;

import com.eirs.lsm.repository.entity.GreylistDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.stream.Stream;


@Repository
public interface GreylistDeviceHisRepository extends JpaRepository<GreylistDevice, Long> {

    public Stream<GreylistDevice> findByCreatedOnBetween(LocalDateTime startDate, LocalDateTime endDate);
}
