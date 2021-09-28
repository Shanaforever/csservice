package com.centricsoftware.core.controller;

import cn.hutool.json.JSONUtil;
import com.centricsoftware.commons.ant.ControllerLog;
import com.centricsoftware.commons.dto.ResEntity;
import com.centricsoftware.commons.dto.WebResponse;
import com.centricsoftware.commons.em.ResCode;
import com.centricsoftware.commons.utils.NodeUtil;
import com.centricsoftware.config.entity.CenterProperties;
import com.centricsoftware.core.service.FileUploadService;
import com.centricsoftware.core.service.TestService;
import com.centricsoftware.core.strategyfactory.CtxServiceFactory;
import com.centricsoftware.core.strategyservice.impl.AbstractDemoStrategyServiceImpl;
import com.centricsoftware.pi.tools.http.C8Communication;
import com.centricsoftware.pi.tools.http.ConnectionInfo;
import com.centricsoftware.pi.tools.util.C8ResponseXML;
import com.centricsoftware.pi.tools.xml.Document;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import com.centricsoftware.rabbitmq.message.MessageStruct;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * 测试控制类
 * @author zheng.gong
 * @date 2020/4/16
 */
@Slf4j
@RequestMapping("/test")
@Controller
public class TestController {
    private final CtxServiceFactory csf;
    public TestController(CtxServiceFactory csf){
        this.csf = csf;
    }
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    AbstractDemoStrategyServiceImpl demoStrategyService;

    @Autowired
    CenterProperties centerProperties;
    @Autowired
    TestService testService;
    @Autowired
    @Qualifier("testFileUploadServiceImpl1")
    FileUploadService fileUploadService;

//    @Autowired
//    private RabbitTemplate rabbitTemplate;

    /**
     * jsp测试
     * @return
     */
    @RequestMapping("/testJsp")
    public String testJsp(){
        return "index";
    }


    @ResponseBody
    @GetMapping("test99")
    public ResEntity test99() throws JsonProcessingException {

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("arraylist-a");
        arrayList.add("arraylist-b");

        getListStr(arrayList);
        return WebResponse.success(ResCode.SUCCESS);
    }

    public String getListStr(ArrayList<String> list) throws JsonProcessingException {
        list.add("a");
        list.add("b");
        list.add("c");
        return objectMapper.writeValueAsString(list);
    }


    @ResponseBody
    @GetMapping("/testRequest")
    @ControllerLog
    public ResEntity testRequest() {
        testService.testServiceDoSth();
        log.info("测试日志info");
        log.warn("测试日志warn");
        log.debug("测试日志debug");
        log.error("测试日志error");
        return WebResponse.success(ResCode.SUCCESS);
    }
    @ResponseBody
    @PostMapping("/testParam")
    public ResEntity testDemo(@RequestBody Map<Object,Object> map){
        log.debug("get json param:{}", JSONUtil.toJsonStr(map));
        Assert.isTrue(map.containsKey("test"),"不包含test");

        return WebResponse.success(ResCode.SUCCESS,map);
    }
    @ResponseBody
    @PostMapping("/testProp")
    public ResEntity testCsProp(){
        Map<String, String> plm = centerProperties.getProp();
        return WebResponse.success(ResCode.SUCCESS,plm);
    }


    /**
     * 策略模式分发请求
     * @param type 请求类型
     * @param params 请求参数
     * @return ResEntity
     * @throws Exception 异常集中捕获，并封装到ResEntity
     */
    @ResponseBody
    @PostMapping("/testDemoService/{type}")
    public ResEntity testDemoService(@PathVariable("type") String type,Map<String, String> params) throws Exception {

        log.debug("get json param:{}", objectMapper.writeValueAsString(params));

        csf.getCurrentContextStrategy(type).process(params);
        return WebResponse.success(ResCode.SUCCESS,params);
    }

//    @PostMapping("testRabbitMQ")
//    public ResEntity testRabbitMQ(String value){
//        log.debug("test rabbitmq message:{}",value);
//        log.debug("消息发送时间：{}",DateUtil.now());
//        rabbitTemplate.convertAndSend(Constants.RabbitConsts.DELAY_MODE_QUEUE, Constants.RabbitConsts.DELAY_QUEUE,
//                new MessageStruct(
//                        "delay message, delay 5s, " + DateUtil
//                                .date()), message -> {
//                    message.getMessageProperties().setHeader("x-delay", 5000);
//                    return message;
//                });
//        return  WebResponse.success(ResCode.SUCCESS);
//    }
    @ResponseBody
    @PostMapping("testCookie")
    public ResEntity testCookie() throws Exception {
        Document document = NodeUtil.queryByXML("<Node Parameter=\"Type\" Op=\"EQ\" Value=\"User\" />");
        List<String> list = C8ResponseXML.resultNodeCNLs(document);
        list.forEach(System.out::println);
        return WebResponse.success(ResCode.SUCCESS,list);
    }
    @ResponseBody
    @PostMapping("logout")
    public ResEntity logout() throws Exception {
        ConnectionInfo info = new ConnectionInfo("192.168.30.129", "Administrator", "c8admin");
        C8Communication.simpleLogout(info);
        return WebResponse.success(ResCode.SUCCESS);
    }

    /**
     * 测试授权请求
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("testAuth")
    public ResEntity testAuth() {
       log.debug("===============授权请求测试===================");
       return WebResponse.success(ResCode.SUCCESS);
    }


}
