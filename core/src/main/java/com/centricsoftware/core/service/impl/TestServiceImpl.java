package com.centricsoftware.core.service.impl;

import com.centricsoftware.commons.ant.ServiceLog;
import com.centricsoftware.core.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TestServiceImpl implements TestService {

    @ServiceLog
    @Override
    public void testServiceDoSth() {
        log.debug("=======test service do sth=======");
        log.info("测试service日志info");
        log.warn("测试service日志warn");
        log.debug("测试service日志debug");
        log.error("测试service日志error");
    }
}
