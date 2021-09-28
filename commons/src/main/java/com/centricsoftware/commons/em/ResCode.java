package com.centricsoftware.commons.em;

import lombok.Getter;

/**
 * 错误码枚举类
 * @author ZhengGong
 * @date 2019/6/12
 */
@Getter
public enum ResCode {
    
    SUCCESS(0,"成功!"),
    OK(200,"OK"),
    ERROR(201,"失败"),
    REQUEST_NOT_FOUND(202, "请求不存在！"),
    HTTP_BAD_METHOD(203, "请求方式不支持！"),
    BAD_REQUEST(204, "请求异常！"),
    PARAM_NOT_MATCH(205, "参数不匹配！"),
    PARAM_NOT_NULL(206, "参数不能为空！"),
    JSON_PARSE_ERROR(207,"JSON转换异常"),
    C8_UNSUPPORTED_ENCODING_ERROR(208,"双编码异常"),
    AUTHORIZE_ERROR(209,"请求未授权！"),
    AUTHORIZE_UP_ERROR(210,"请求未授权,账号或密码错误！"),
    SYSTEM_RUNTIME_ERROR(999,"系统异常！");

    private Integer code;

    private String message;

    ResCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 通过code返回枚举
     * @param code
     * @return
     */
    public static ResCode parse(Integer code){
        ResCode[] values = values();
        for (ResCode value : values) {
            if(value.getCode().equals(code)){
                return value;
            }
        }
        throw  new RuntimeException("Unknown code of ResultEnum");
    }
}
