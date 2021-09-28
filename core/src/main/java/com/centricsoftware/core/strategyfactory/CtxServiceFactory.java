package com.centricsoftware.core.strategyfactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: spring在对map注入时，会将类名作为key,实现不同的service调用同一个接口实现不同的方案
 * @Author: ZhengGong
 * @CreateDate: 2019/5/22 13:59
 * @Company: Centric
 */
@Component
public class CtxServiceFactory {
    private final Map<String, BaseCtxStrategyService> contextStrategy = new ConcurrentHashMap<>();
    @Autowired
    public CtxServiceFactory(Map<String, BaseCtxStrategyService> contextStrategy){
        contextStrategy.forEach((this.contextStrategy::put));
    }

    public BaseCtxStrategyService getCurrentContextStrategy(String type) {
        Assert.notNull(type,"type is null");
        return this.contextStrategy.get(type);
    }


}
