package com.centricsoftware.commons.dto;

import com.centricsoftware.commons.em.ResCode;
import com.centricsoftware.commons.exception.BaseException;

/**
 * @Description: PLM工具类异常基类
 * @Author: ZhengGong
 * @CreateDate: 2019/5/20 15:11
 * @Company: Centric
 */
public class PlmUtilsException extends BaseException {

    public PlmUtilsException(ResCode code) {
        super(code);
    }

    public PlmUtilsException(ResCode code, Object data) {
        super(code,data);
    }

    public PlmUtilsException(Integer code, String message) {
        super(code,message);
    }

    public PlmUtilsException(Integer code, String message, Object data) {
        super(code,message,data);
    }

    public PlmUtilsException(ResCode code, Throwable e){
        super(code);
    }


}
