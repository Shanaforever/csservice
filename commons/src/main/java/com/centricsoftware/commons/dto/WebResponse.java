package com.centricsoftware.commons.dto;


import com.centricsoftware.commons.em.ResCode;
import com.centricsoftware.commons.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.stringtemplate.v4.ST;

import java.net.URLEncoder;

/**
 * 接口返回信息
 * @author ZhengGong
 * @date 2019/9/16
 */
@Slf4j
public class WebResponse {

    /**
     * 失败返回特定的消息实体
     * @param code 错误代码
     * @param message 错误信息
     * @param data 具体消息实体
     * @return 消息实体ResEntity
     */
    public static  ResEntity failure(Integer code, String message, Object data) {
        return ResEntity.builder().code(code).msg(message).data(data).success(false).build();
    }

    /**
     * 失败返回特定的消息实体
     * @param code 错误代码
     * @param message 错误信息
     * @return 消息实体ResEntity
     */
    public static  ResEntity failure(Integer code, String message) {
        return ResEntity.builder().code(code).msg(message).success(false).build();
    }

    /**
     * 失败返回特定的消息实体
     * @param respCode 错误代码封装枚举类
     * @param data 具体消息实体
     * @return 消息实体ResEntity
     */
    public static  ResEntity failure(ResCode respCode, Object data) {
        return getStringObjectMap(respCode, data,false);
    }

    /**
     * 失败返回特定的消息实体
     * @param respCode 错误代码封装枚举类
     * @return 消息实体ResEntity
     */
    public static  ResEntity failure(ResCode respCode) {
        return getStringObjectMap(respCode,false);
    }

    /**
     * 失败返回特定的消息实体
     * @param e 错误基类
     * @return 消息实体ResEntity
     */
    public static<T extends BaseException> ResEntity failure(T e){
        return failure(e.getCode(),e.getMessage(),e.getData());
    }



    /**
     * 错误302到具体的页面
     * @param stp 模板链接
     * @param msg 错误信息
     * @return 页面链接
     */
    public static String failurePage(String stp,String msg)  {
        try {
            ST st=new ST(stp);
            st.add("ERROR", URLEncoder.encode(msg,"UTF-8"));
            return  "redirect:"+st.render();
        } catch (Exception e) {
            log.error("error:",e);
            return null;
        }
    }


    /**
     * 成功返回特定的状态码和信息
     * @param respCode 成功代码封装枚举类
     * @param data 具体消息实体
     * @return 消息实体ResEntity
     */
    public static  ResEntity success(ResCode respCode, Object data) {
        return getStringObjectMap(respCode, data,true);
    }

    private static  ResEntity getStringObjectMap(ResCode respCode, Object data, Boolean success) {
        return ResEntity.builder().code( respCode.getCode()).msg( respCode.getMessage()).data(data).success(success).build();
    }

    /**
     * 成功返回特定的状态码和信息
     * @param respCode 成功代码封装枚举类
     * @return 消息实体ResEntity
     */
    public static  ResEntity success(ResCode respCode) {
        return getStringObjectMap(respCode,true);
    }

    private static ResEntity getStringObjectMap(ResCode respCode, Boolean success) {
        return ResEntity.builder().code(respCode.getCode()).msg(respCode.getMessage()).success(success).build();
    }



}
