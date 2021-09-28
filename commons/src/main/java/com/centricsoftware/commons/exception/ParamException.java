package com.centricsoftware.commons.exception;

import com.centricsoftware.commons.em.ResCode;

/**
 * 参数异常
 * @author ZhengGong
 * @date 2019/9/16
 */
public class ParamException extends BaseException {
    public ParamException(ResCode code) {
        super(code);
    }

    public ParamException(ResCode code, Object data) {
        super(code,data);
    }

    public ParamException(Integer code, String message) {
        super(code,message);
    }

    public ParamException(Integer code, String message, Object data) {
        super(code,message,data);
    }

    public ParamException(ResCode code, Throwable e){
        super(code);
    }
}
