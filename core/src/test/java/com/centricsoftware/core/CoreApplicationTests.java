package com.centricsoftware.core;

import cn.hutool.core.lang.Console;
import com.centricsoftware.commons.em.ResCode;
import com.centricsoftware.config.entity.CenterProperties;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;

import java.util.ArrayList;

//import org.jasypt.encryption.StringEncryptor;


@Slf4j
@SpringBootTest
@ComponentScan(value="com.centricsoftware")
class CoreApplicationTests {

    @Value("${web.port}")
    String port;
//    @Value("${spring.redis.host}")
//    String redisHost;
    @Autowired
    CenterProperties centerProperties;
    @Test
    void contextLoads() {
        log.warn("port:{}",port);
//        log.warn("redis host:{}",redisHost);
        log.info("test info");
        log.debug("test debug");
        log.error("test error");
        log.info("cons:{}", ResCode.BAD_REQUEST.getMessage());
    }

    @Test
    void testAssert(){
        ArrayList<Integer> list = Lists.newArrayList(1, 2, 3, 4, null, 5);
        list.forEach(i->{
            Console.log("i:{}",i);
            Assert.isNull(i,"数据为空！");
        });


    }

    @Test
    void testNodeUtil() throws Exception {

    }
    /*
    如果不需要加载redis模块，则需要
        1. 在pom.xml(plmservice)中注释掉<module>redis</module>
        2. 在pom.xml(core)中注释掉
            <dependency>
                <groupId>com.centricsoftware</groupId>
                <artifactId>redis</artifactId>
            </dependency>
        3.重新编译plmservice即可

     */

//
//    @Autowired
//    RedisExecutor redisExecutor;
//
//    /**
//     * 测试 Redis 操作
//     */
//    @Test
//    public void redisTest() {
//        redisExecutor.set("k1","v2", Constants.Redis.C8INTERFACE_EXPIRE_TIME);
//        Object k1 = redisExecutor.get("k1");
//        log.info("get key:{}",k1);
//    }

//    @Autowired
//    private StringEncryptor encryptor;
//    /**
//     * 密码加密
//     */
//    @Test
//    public void encode() {
//        String applicationName = "c8admin";
//        System.out.println(encryptor.encrypt(applicationName));
//    }

}
