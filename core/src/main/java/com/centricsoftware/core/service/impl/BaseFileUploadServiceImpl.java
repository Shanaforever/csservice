package com.centricsoftware.core.service.impl;

import com.centricsoftware.commons.dto.ResEntity;
import com.centricsoftware.commons.dto.WebResponse;
import com.centricsoftware.commons.em.ResCode;
import com.centricsoftware.core.service.BaseFileUploadService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseFileUploadServiceImpl implements BaseFileUploadService {
    @Override
    public ResEntity doSth(String file) {
        log.debug("======================自定义文件上传PLM前调用的方法=====================");
        return WebResponse.success(ResCode.SUCCESS);
    }
}
