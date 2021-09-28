package com.centricsoftware.core.controller;

import com.centricsoftware.commons.dto.ResEntity;
import com.centricsoftware.core.strategyfactory.CtxServiceFactory;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 控制层分发类，此类CtxServiceFactory方法不要动，用于调用不同的后台service，如果需要增加传入参数，请新增一个方法
 * 此类主要用于同一个功能的不同实现，不可替代Controller功能
 * @author ZhengGong
 * @date 2019/5/24
 */
@RequestMapping("/watchService")
@RestController
@Slf4j
public class DispatchController {
    private final CtxServiceFactory csf;
    @Autowired
    public DispatchController(CtxServiceFactory csf){
        this.csf = csf;
    }

    /**
     * 这个是策略模式核心入口，请不要动此方法，请求需要增加参数或者改变请求方式，请在下方增加新的自定义方法
     * 注意这里已经将参数变为map格式，所以系统底层不需要更改接口和实现类。在实际使用中通过map.get()获得所需要的参数
     * @param type 指定的service名字
     * @param params 发送的json包体
     * @return ResEntity
     */

    @ResponseBody
    @PostMapping("/post/{type}")
    public ResEntity plmServiceForward(@PathVariable("type") String type,
                                       @RequestBody Map<String,String> params) throws Exception{
        return csf.getCurrentContextStrategy(type).process(params);
    }

//    @ResponseBody
//    @GetMapping("/get/{type}")
//    public ResEntity plmServiceForwardGet(@PathVariable("type") String type,
//                                       @RequestBody(required = false) Map<String,String> params) throws Exception{
//        return csf.getCurrentContextStrategy(type).process(params);
//    }

    @ResponseBody
    @GetMapping("/get/{type}")
    public ResEntity plmServiceForwardGet(@PathVariable("type") String type,String user,String data) throws Exception{
        HashMap<String, String> params = Maps.newHashMap();
        params.put("user",user);
        params.put("data",data);
        return csf.getCurrentContextStrategy(type).process(params);
    }




}
