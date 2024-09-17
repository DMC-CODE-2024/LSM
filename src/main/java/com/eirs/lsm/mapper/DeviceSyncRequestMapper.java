package com.eirs.lsm.mapper;

import com.eirs.lsm.dto.OperatorRequestDTO;
import com.eirs.lsm.repository.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeviceSyncRequestMapper {

    @Mapping(source = "createdOn", target = "requestDate")
    OperatorRequestDTO toOperatorRequestDTO(AllowedTac allowedTac);

    @Mapping(source = "createdOn", target = "requestDate")
    OperatorRequestDTO toOperatorRequestDTO(BlockedTac blockedTac);

    @Mapping(source = "createdOn", target = "requestDate")
    OperatorRequestDTO toOperatorRequestDTO(BlacklistDevice device);

    @Mapping(source = "createdOn", target = "requestDate")
    OperatorRequestDTO toOperatorRequestDTO(GreylistDevice device);

    @Mapping(source = "createdOn", target = "requestDate")
    OperatorRequestDTO toOperatorRequestDTO(ExceptionDevice device);

    OperatorRequestDTO toOperatorRequestDTO(DeviceSyncRequest request);

    DeviceSyncRequest toDeviceSyncRequest(OperatorRequestDTO dto);

}
