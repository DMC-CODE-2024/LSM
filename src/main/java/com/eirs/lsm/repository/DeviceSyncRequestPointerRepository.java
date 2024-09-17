package com.eirs.lsm.repository;

import com.eirs.lsm.repository.entity.DeviceSyncRequestListIdentity;
import com.eirs.lsm.repository.entity.DeviceSyncRequestPointer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface DeviceSyncRequestPointerRepository extends JpaRepository<DeviceSyncRequestPointer, Long> {

    @Query("SELECT d FROM DeviceSyncRequestPointer d WHERE d.listType=:listType")
    DeviceSyncRequestPointer findLastTillDate(@Param("listType") DeviceSyncRequestListIdentity listType);


}
