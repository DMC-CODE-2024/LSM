package com.eirs.lsm.validator;

import com.eirs.lsm.dto.OperatorRequestDTO;
import com.eirs.lsm.service.SystemConfigurationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class Validator {

    public void validate(OperatorRequestDTO dto) {

    }

    public boolean isOperatorRequest(OperatorRequestDTO dto) {
        if (StringUtils.isBlank(dto.getOperatorName()) || SystemConfigurationService.operatorAll.equalsIgnoreCase(dto.getOperatorName()))
            return false;
        return true;
    }
}
