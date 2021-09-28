package com.centricsoftware.mybatis.entity;

import java.util.List;

/**
 * StyleBO
 * 业务对象，领域模型，款式
 * @author zheng.gong
 * @date 2021/1/11
 */
public class StyleBO {
    private boolean active;
    private String nodeName;
    private String code;
    private String description;

    private List<ApparellBom> bomList;
}
