package com.centricsoftware.mybatis.provider;

import org.apache.ibatis.jdbc.SQL;

/**
 * 自定义sql生成器
 * @author zheng.gong
 * @date 2020/5/6
 */
public class PlmStyleSqlProvider {


    public String queryStyleById(){
        SQL sql = new SQL();
        sql.SELECT("id,image_url,style_no,style_name")
                .FROM("plm_style a")
                .LEFT_OUTER_JOIN("#{styleName} b")
                .WHERE("style_no='X1940002'")
                .WHERE("style_name=#{styleName}");
        return sql.toString();
    }

    public String queryStyleById1(){
        SQL sql = new SQL();
        sql.SELECT("id,image_url,style_no,style_name")
                .FROM("plm_style a")
                .LEFT_OUTER_JOIN("#{styleName} b")
                .WHERE("style_no='X1940002'")
                .WHERE("style_name=#{styleName}");
        return sql.toString();
    }
    public String queryStyleById2(){
        SQL sql = new SQL();
        sql.SELECT("id,image_url,style_no,style_name")
                .FROM("plm_style a")
                .LEFT_OUTER_JOIN("#{styleName} b")
                .WHERE("style_no='X1940002'")
                .WHERE("style_name=#{styleName}");
        return sql.toString();
    }
}
