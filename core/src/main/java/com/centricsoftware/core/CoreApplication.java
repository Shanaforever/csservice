package com.centricsoftware.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
/**
 * 代码入口
 * @author zheng.gong
 * @date 2020/4/21
 */
@ComponentScan("com.centricsoftware")
@EnableScheduling
@SpringBootApplication
public class CoreApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CoreApplication.class);
    }
    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }

}
