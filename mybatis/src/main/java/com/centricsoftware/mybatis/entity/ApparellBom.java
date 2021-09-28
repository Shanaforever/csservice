package com.centricsoftware.mybatis.entity;

import java.util.List;

/**
 * ApparellBom
 * 领域模型，款式bom
 * @author zheng.gong
 * @date 2021/1/11
 */
public class ApparellBom {
    private String nodeName;
    private String code;
    private String description;
    private Integer order;
    private List<MaterialBO> materialBOList;
}
