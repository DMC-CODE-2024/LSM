package com.eirs.lsm.repository;

import com.eirs.lsm.repository.entity.AllowedTac;
import com.eirs.lsm.repository.entity.BlacklistDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Stream;


@Repository
public interface AllowTacHisRepository extends JpaRepository<AllowedTac, Long> {

    public Stream<AllowedTac> findByCreatedOnBetween(LocalDateTime startDate, LocalDateTime endDate);
}
