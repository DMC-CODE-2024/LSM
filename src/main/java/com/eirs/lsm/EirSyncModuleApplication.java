package com.eirs.lsm;

import com.eirs.lsm.client.OperatorProcessFactory;
import com.eirs.lsm.orchestration.*;
import com.eirs.lsm.service.SystemConfigurationService;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

@SpringBootApplication
@EnableEncryptableProperties
public class EirSyncModuleApplication {

    public static void main(String[] args) throws Exception {

        ApplicationContext context = SpringApplication.run(EirSyncModuleApplication.class, args);
        if (CollectionUtils.isEmpty(context.getBean(SystemConfigurationService.class).getOperators()))
            throw new RuntimeException("config missing [enable.operators:]");

        context.getBean(OperatorProcessFactory.class).init();

        context.getBean(AllowedTacOrchestration.class).init();
        context.getBean(BlacklistDeviceOrchestration.class).init();
        context.getBean(TrackedListDeviceOrchestration.class).init();
        context.getBean(ExceptionDeviceOrchestration.class).init();
        context.getBean(BlockedTacOrchestration.class).init();

    }

}
