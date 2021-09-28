package com.centricsoftware.commons.dto;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 返回信息实体类
 * @author ZhengGong
 * @date 2020/4/16
 */
@Data
@Builder
public class ResEntity implements Serializable {
    /**
     * 错误编号
     */
    private Integer code;
    /**
     * 错误信息
     */
    private String msg;
     /**
     * 返回对象
     */
    private Object data;
    /**
     * 是否成功
     */
    private boolean success;
}
