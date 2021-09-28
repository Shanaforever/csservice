package com.centricsoftware.commons.exception;

import com.centricsoftware.commons.em.ResCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 异常基类
 * @author ZhengGong
 * @date 2019/6/18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseException extends RuntimeException {
    private Integer code;
    private String message;
    private Object data;

    public BaseException(ResCode resCode) {
        super(resCode.getMessage());
        this.code = resCode.getCode();
        this.message = resCode.getMessage();
    }

    public BaseException(ResCode resCode, Object data) {
        this(resCode);
        this.data = data;
    }

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BaseException(Integer code, String message, Object data) {
        this(code, message);
        this.data = data;
    }

    public BaseException(ResCode code, Throwable e){
        this(code);
        data = e;
    }

    public ResCode getResCode(){
        return ResCode.parse(code);
    }



}
