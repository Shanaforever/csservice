package com.centricsoftware.core;


//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.centricsoftware.mybatis.entity.PlmStyleEntity;
//import com.centricsoftware.mybatis.mapper.PlmStyleMapper;
//import com.centricsoftware.mybatis.service.impl.StyleServiceImpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.centricsoftware.mybatis.entity.PlmStyleEntity;
import com.centricsoftware.mybatis.mapper.PlmStyleMapper;
import com.centricsoftware.mybatis.service.impl.StyleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootTest
@EnableAutoConfiguration
//@EncryptablePropertySource(name = "EncryptedProperties", value="classpath:application-mybatis.yml")
@ComponentScan(value="com.centricsoftware")
public class MybatisPlusTest {
    @Autowired
    PlmStyleMapper styleMapper;

    @Autowired
    StyleServiceImpl styleService;

    /**
     * 分页查询
     */
    @Test
    public void testPage(){
        Page<PlmStyleEntity> page = new Page<>(1, 5);
        IPage<PlmStyleEntity> stylePage = styleService.page(page);
        System.out.println("总页数： "+stylePage.getPages());
        System.out.println("总记录数： "+stylePage.getTotal());
        stylePage.getRecords().forEach(System.out::println);
    }
//    /**
//     * 通过通用查询方式查询
//     */
//    @Test
//    public void test(){
//        PlmStyleEntity plmStyleEntity = styleMapper.selectById(1);
//
//        QueryWrapper<PlmStyleEntity> wrapper = new QueryWrapper<PlmStyleEntity>();
//        log.info("style:{}",plmStyleEntity);
//    }
//
//    /**
//     * 通过自定义sql生成器查询
//     */
//    @Test
//    public void test1(){
//        PlmStyleEntity plmStyleEntity = styleMapper.queryByProvider("X1940002");
//        log.info("style:{}",plmStyleEntity);
//    }
//
//    @Autowired
//    StyleService styleService;
//    /**
//     * 通过stleservice查询
//     */
//    @Test
//    public void test2(){
//        PlmStyleEntity styleEntity = styleService.getById(1);
//        log.info("style:{}",styleEntity);
//    }







}
