package com.centricsoftware.commons.exception;

import com.centricsoftware.commons.em.ResCode;

/**
 * c8 uri双编码异常
 * @author zheng.gong
 * @date 2020/5/12
 */
public class C8UnsupportedEncodingException extends BaseException {
    public C8UnsupportedEncodingException(ResCode code) {
        super(code);
    }

    public C8UnsupportedEncodingException(ResCode code, Object data) {
        super(code,data);
    }

    public C8UnsupportedEncodingException(Integer code, String message) {
        super(code,message);
    }

    public C8UnsupportedEncodingException(Integer code, String message, Object data) {
        super(code,message,data);
    }

    public C8UnsupportedEncodingException(ResCode code, Throwable e){
        super(code);
    }
}
