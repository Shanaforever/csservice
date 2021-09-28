package com.centricsoftware.commons.ant;

import java.lang.annotation.*;

/**
 * 自定义注解，拦截service
 * @author ZhengGong
 * @date 2019/6/25
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceLog {
    String value() default "";
}
