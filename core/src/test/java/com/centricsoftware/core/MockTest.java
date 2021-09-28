package com.centricsoftware.core;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class MockTest {
    @Autowired
    MockMvc mockMvc;


    /**
     * 模拟http调用，断言返回结果
     * @throws Exception
     */
    @Test
    public void test1() throws Exception{

        Map<String, String> map = Maps.newHashMap();
        map.put("a","1");
        ObjectMapper objectMapper = new ObjectMapper();
        //调用demoStrategyService1#process方法，mock模拟返回一条数据
//        Mockito.when(demoStrategyService1.process(map)).thenReturn(WebResponse.success(ResCode.SUCCESS, "模拟数据"));
        //模拟http调用
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/watchService/post" +
                "/DemoStrategyService1Impl").contentType(MediaType.APPLICATION_JSON).characterEncoding(CharsetUtil.UTF_8)
                .content(objectMapper.writeValueAsString(map)));

        //匹配json属性数据
//        resultActions.andExpect(MockMvcResultMatchers.jsonPath("data", IsEqual.equalTo("模拟数据")));
        //设置返回字符集utf-8
        resultActions.andReturn().getResponse().setCharacterEncoding(CharsetUtil.UTF_8);
        //匹配整个json
        resultActions.andExpect(MockMvcResultMatchers.content().json("{\"code\":0,\"msg\":\"成功!\"," +
                "\"data\":{\"a\":\"1\"},\"success\":true}"));
        //添加断言
        resultActions.andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());

    }

    /**
     * 模拟http请求，测试native export
     */
    @Test
    public void test3() throws Exception{
        String params = JSONUtil.createObj()
                .set("url", "C1010")
                .set("user", "Administrator")
                .toString();
        String result = mockMvc.perform(MockMvcRequestBuilders
                .post("/watchService/post/DemoStrategyService1Impl").content(params)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(CharsetUtil.UTF_8)
        )
                .andReturn()
                .getResponse()
                .getContentAsString(CharsetUtil.CHARSET_UTF_8);

        Console.log("result={}",result);
    }

    /**
     * 模拟service返回
     */
    @Test
    public void test2(){
//        Map<String, String> map = Maps.newHashMap();
//        map.put("a","1");
//        //调用demoStrategyService1#process方法，mock模拟返回一条数据
//        Mockito.when(demoStrategyService1.process(map)).thenReturn(WebResponse.success(ResCode.SUCCESS, "模拟数据"));
//
//        ResEntity process = demoStrategyService1.process(map);
//
//        String s = JSONUtil.toJsonStr(process);
//        System.out.println(s);
    }
}