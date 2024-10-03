package com.eirs.lsm;

import com.eirs.lsm.client.OperatorProcessFactory;
import com.eirs.lsm.orchestration.*;
import com.eirs.lsm.repository.ConfigRepository;
import com.eirs.lsm.service.SystemConfigurationService;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

@SpringBootApplication
@EnableEncryptableProperties
public class EirSyncModuleApplication {
    private static final Logger log = LoggerFactory.getLogger(EirSyncModuleApplication.class);

    public static void main(String[] args) throws Exception {

        ApplicationContext context = SpringApplication.run(EirSyncModuleApplication.class, args);
        if (CollectionUtils.isEmpty(context.getBean(SystemConfigurationService.class).getOperators())) {
            log.error("config missing [LSM_NO_OF_OPERATORS]");
            System.exit(0);
        }
        context.getBean(OperatorProcessFactory.class).init();

        context.getBean(AllowedTacOrchestration.class).init();
        context.getBean(BlacklistDeviceOrchestration.class).init();
        context.getBean(TrackedListDeviceOrchestration.class).init();
        context.getBean(ExceptionDeviceOrchestration.class).init();
        context.getBean(BlockedTacOrchestration.class).init();

    }

}
