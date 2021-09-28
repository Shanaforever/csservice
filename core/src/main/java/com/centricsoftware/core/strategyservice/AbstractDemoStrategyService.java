package com.centricsoftware.core.strategyservice;


import com.centricsoftware.commons.dto.ResEntity;
import com.centricsoftware.core.strategyfactory.BaseCtxStrategyService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j

public abstract class AbstractDemoStrategyService implements BaseCtxStrategyService {

    @Override
    public ResEntity process(Map<String, String> params) throws Exception {
        String configName = params.get("configName");
        return null;
    }

    public String add(int i){
        return "";
    }

}
