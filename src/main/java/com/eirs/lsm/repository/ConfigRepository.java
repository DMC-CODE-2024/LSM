package com.eirs.lsm.repository;

import com.eirs.lsm.repository.entity.SysParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigRepository extends JpaRepository<SysParam, Long> {

    public Optional<SysParam> findByConfigKeyIgnoreCaseAndModule(String configKey, String module);
}
