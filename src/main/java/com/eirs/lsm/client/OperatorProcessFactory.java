package com.eirs.lsm.client;

import com.eirs.lsm.repository.entity.SystemConfigKeys;
import com.eirs.lsm.service.SystemConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.concurrent.*;

@Component
public class OperatorProcessFactory {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationContext context;

    @Autowired
    private SystemConfigurationService config;

    private Map<String, OperatorProcessService> map = new ConcurrentHashMap<>();

    private Map<String, OperatorUrlDelegate> operatorUrlMap = new ConcurrentHashMap<>();

    public void init() throws Exception {
        if (CollectionUtils.isEmpty(config.getOperators()))
            throw new RuntimeException("Operators List must not empty");

        for (String operator : config.getOperators()) {
            Integer operatorNoOfEirs = config.getNoOfEirs(operator);
            for (int i = 1; i <= operatorNoOfEirs; i++) {
                OperatorProcessService process = context.getBean(OperatorProcessService.class);
                process.setOperator(operator);
                process.setOperatorEirId(i);
                map.put(operator, process);

                OperatorUrlDelegate operatorUrlService = context.getBean(OperatorUrlDelegate.class);
                operatorUrlService.setOperator(operator);
                operatorUrlService.setOperatorEirId(i);
                operatorUrlService.init();

                process.setOperatorUrlService(operatorUrlService);
                log.info("Operator:{} , OperatorEirId:{} Process:{} In Factory", operator, i, process);
                process.init();
            }
        }
    }

}
