package com.centricsoftware.core.strategyservice.impl;

import com.centricsoftware.commons.dto.ResEntity;
import com.centricsoftware.commons.dto.WebResponse;
import com.centricsoftware.commons.em.ResCode;
import com.centricsoftware.core.strategyservice.AbstractDemoStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 测试转发类2
 * 该类将通过{@link com.centricsoftware.core.controller.DispatchController} 中的type找到
 * @author zheng.gong
 * @date 2020/4/21
 */
@Slf4j
@Service("DemoStrategyService2Impl")
public class AbstractDemoStrategyService2Impl extends AbstractDemoStrategyService {


    @Override
    public ResEntity process(Map<String, String> params) {
        log.debug("--------------------DemoStrategyService2     Impl---------------------------");
        return WebResponse.success(ResCode.SUCCESS, params);
    }



}
