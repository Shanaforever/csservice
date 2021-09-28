package com.centricsoftware.commons.ant;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 自定义注解，拦截Controller
 * @author ZhengGong
 * @date 2019/6/25
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ControllerLog {
    String value() default "";
}
