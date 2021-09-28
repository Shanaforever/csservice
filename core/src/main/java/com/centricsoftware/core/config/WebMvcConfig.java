package com.centricsoftware.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域处理
 * @author zheng.gong
 * @date 2020/4/16
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private static final long MAX_AGE_SECS = 3600;


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE")
                .maxAge(MAX_AGE_SECS);
    }

    /**
     * 注册拦截器
     * @param registry InterceptorRegistry
     */
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new SimpleAuthInterceptor()).addPathPatterns(interceptLIst());
//    }
//
//    public List<String> interceptLIst(){
//        return Lists.newArrayList("/test/testAuth");
//    }
}
