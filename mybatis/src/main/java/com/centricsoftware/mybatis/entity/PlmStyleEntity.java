package com.centricsoftware.mybatis.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * 款式实体demo
 * @author zheng.gong
 * @date 2020/4/20
 */
@TableName("plm_style")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlmStyleEntity {
    /**
     * id
     */
    @JsonProperty("id")
    private Integer id;

    /**
     * 图片
     */
    @JsonProperty("image_url")
    private String imageUrl;

    /**
     * 款号
     */
    @JsonProperty("style_no")
    private String styleNo;

    /**
     * 款式
     */
    @JsonProperty("style_name")
    private String styleName;

    /**
     * 品牌季节
     */
    @JsonProperty("season")
    private String season;

    /**
     * 上市波段
     */
    @JsonProperty("category1")
    private String category1;

    /**
     * 大类
     */
    @JsonProperty("large_category")
    private String largeCategory;

    /**
     * 小类
     */
    @JsonProperty("small_category")
    private String smallCategory;

    /**
     * 系列
     */
    @JsonProperty("proseries")
    private String proseries;

    /**
     * 主题类型
     */
    @JsonProperty("theme_type")
    private String themeType;

    /**
     * 系列主题
     */
    @JsonProperty("proseries_theme")
    private String proseriesTheme;

    /**
     * 特殊工艺
     */
    @JsonProperty("special_tec")
    private String specialTec;

    /**
     * 设计组
     */
    @JsonProperty("category2")
    private String category2;

    /**
     * 设计师
     */
    @JsonProperty("designe")
    private String designe;

    /**
     * 设计要求
     */
    @JsonProperty("design_req")
    private String designReq;

    /**
     * 吊牌价
     */
    @JsonProperty("price")
    private String price;

    /**
     * 销售量
     */
    @JsonProperty("total")
    private String total;

    /**
     * 销售额
     */
    @JsonProperty("accounts")
    private String accounts;

    /**
     * 创建
     */
    @JsonProperty("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private String createTime;

    /**
     * 配色
     */
    @JsonProperty("color_image_url")
    private String colorImageUrl;

    /**
     * 状态
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonProperty("status")
    private String status;

    /**
     * 销售排名图片url
     */
    @JsonProperty("rank_image_url")
    private String rankImageUrl;

}
