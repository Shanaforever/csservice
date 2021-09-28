package com.centricsoftware.core.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * AopAspect对controller和service类或者带有自定义注解的类增强减少log代码冗余
 * @author ZhengGong
 * @date 2020/4/16
 */
@Aspect
@Component
@Slf4j
public class AopAspect {

    @Pointcut(value = "(execution(* com.centricsoftware.core.controller..*(..))||@annotation(com.centricsoftware.commons.ant.ControllerLog))")
    public void controllerAspect(){}

    @Pointcut("execution(* com.centricsoftware.task.service.*.*(..))||@annotation(com.centricsoftware.commons.ant.ServiceLog)")
    public void serviceAspcet(){}
    /**
     * 对Controller环绕增强
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("controllerAspect()")
    public Object controllerAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("---------------------Controller方法调用开始-----------------------");
        getControllerArgsDescription(joinPoint);
        Instant t1 = Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8));
        Object proceed = joinPoint.proceed();
        Instant t2 = Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8));
        Duration between = Duration.between(t1, t2);
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        log.warn("{}执行开始时间:{},结束时间:{},耗时{}ms",methodSignature.getMethod(),t1,t2,between.toMillis());
        log.debug("方法{}执行成功，返回结果{}",methodSignature.getMethod().getName(),proceed);
        log.info("---------------------Controller方法调用结束-----------------------");
        return proceed;
    }

    /**
     * 对Service环绕增强
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("serviceAspcet()")
    public Object serviceAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("---------------------Service方法调用 开始-----------------------");
        getServiceArgsDescription(joinPoint);
        Instant t1 = Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8));
        Object proceed = joinPoint.proceed();
        Instant t2 = Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8));
        Duration between = Duration.between(t1, t2);
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        log.debug("{}执行开始时间:{},结束时间:{},耗时{}ms",methodSignature.getMethod(),t1,t2,between.toMillis());
        log.debug("方法{}执行成功，返回结果{}",methodSignature.getMethod(),proceed);
        log.debug("---------------------Service方法调用 结束-----------------------");
        return proceed;
    }


    public void getControllerArgsDescription(JoinPoint joinPoint){
        //1.获取到所有的参数值的数组
        Object[] args = joinPoint.getArgs();
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        //2.获取到方法的所有参数名称的字符串数组
        String[] parameterNames = methodSignature.getParameterNames();
        //使用自定义注解中的属性
        Method method = methodSignature.getMethod();
//        ControllerLog controllerLog = method.getAnnotation(ControllerLog.class);

        log.info("method name:{}",method.toString());
        for (int i =0 ,len=parameterNames.length;i < len ;i++){
            log.info("参数名：{}，参数值：{}",parameterNames[i],args[i]);
        }

    }

    public static void getServiceArgsDescription(JoinPoint joinPoint){
        //1.获取到所有的参数值的数组
        Object[] args = joinPoint.getArgs();
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        //2.获取到方法的所有参数名称的字符串数组
        String[] parameterNames = methodSignature.getParameterNames();
        log.debug("---------------参数列表---------------------");
        for (int i =0 ,len=parameterNames.length;i < len ;i++){
            log.debug("参数名：{}，参数值：{}",parameterNames[i],args[i]);

        }
    }


}
