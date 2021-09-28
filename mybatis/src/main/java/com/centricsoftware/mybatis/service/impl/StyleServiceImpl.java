package com.centricsoftware.mybatis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.centricsoftware.mybatis.entity.PlmStyleEntity;
import com.centricsoftware.mybatis.mapper.PlmStyleMapper;
import com.centricsoftware.mybatis.service.StyleService;
import org.springframework.stereotype.Service;

@Service
public class StyleServiceImpl extends ServiceImpl<PlmStyleMapper, PlmStyleEntity> implements StyleService {
}
