package com.centricsoftware.mybatis.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.centricsoftware.mybatis.entity.PlmStyleEntity;
import com.centricsoftware.mybatis.provider.PlmStyleSqlProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 测试款式mapper`
 * @author ChangJiang
 * @date 2020/5/6
 */
@Component
public interface PlmStyleMapper extends BaseMapper<PlmStyleEntity> {
    /**
     * 将删除的数据还原，用于开发测试
     */
    @Update("update plm_style set status=0")
    void restore();

    /**
     * 动态查询
     * @param wrapper 动态查询条件
     * @return List<PlmStyleManageEntity>
     */
    @Select("select * from plm_style ${ew.customSqlSegment}")
    List<PlmStyleEntity> queryByDynamicCondition(@Param("ew") QueryWrapper wrapper);

    @SelectProvider(type= PlmStyleSqlProvider.class,method = "queryStyleById")
    PlmStyleEntity queryByProvider(String styleName,String tableName);
}
