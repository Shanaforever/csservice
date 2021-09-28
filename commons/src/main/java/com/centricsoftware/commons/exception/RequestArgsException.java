package com.centricsoftware.commons.exception;

import com.centricsoftware.commons.em.ResCode;
import com.centricsoftware.commons.exception.BaseException;

/**
 * 请求参数异常
 * @author ZhengGong
 * @date 2019/9/16
 */
public class RequestArgsException extends BaseException {
    public RequestArgsException(ResCode resCode) {
        super(resCode);
    }

    public RequestArgsException(ResCode resCode, Object data) {
        super(resCode, data);
    }

    public RequestArgsException(Integer code, String message) {
        super(code, message);
    }

    public RequestArgsException(Integer code, String message, Object data) {
        super(code, message, data);
    }

    public RequestArgsException(ResCode code, Throwable e) {
        super(code, e);
    }
}
