package com.centricsoftware.core.service.impl;

import cn.hutool.core.lang.Dict;
import com.centricsoftware.commons.dto.ResEntity;
import com.centricsoftware.commons.dto.WebResponse;
import com.centricsoftware.commons.em.ResCode;
import com.centricsoftware.core.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TestFileUploadServiceImpl2 implements FileUploadService {
    @Override
    public ResEntity doSth(String file) {
        log.debug("---------------------文件上传自定义实现2-------------------------");
        log.debug("file : {}",file);
        Dict file1 = Dict.create().set("file", file);
        return WebResponse.success(ResCode.SUCCESS,file1);
    }
}
