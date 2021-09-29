package com.centricsoftware.core.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.centricsoftware.commons.dto.ResEntity;
import com.centricsoftware.commons.dto.WebResponse;
import com.centricsoftware.commons.em.ResCode;
import com.centricsoftware.commons.exception.BaseException;
import com.centricsoftware.commons.utils.SpringUtil;
import com.centricsoftware.core.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/uploadController")
@RestController
public class FileUploadController {

    /**
     * 文件上传总入口
     * @param file 模拟文件上传 MultipartFile
     * @param beanName 实现类名字
     * @return 返回实现类结果
     */
    @PostMapping("upload")
    public ResEntity upload(String file,String beanName){

        Object bean = SpringUtil.getBean(beanName);
        if(bean instanceof FileUploadService){
            FileUploadService service = (FileUploadService) bean;
            ResEntity resEntity = service.doSth(file);
            return resEntity;
        }else{
            throw new BaseException(ResCode.ERROR,"传入的beanName没有继承FileUploadService");
        }
    }
    @PostMapping("/upload1")
    public ResEntity upload1(MultipartFile file) throws IOException {
        log.debug("======================自定义功能实现======================");
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
        List<Map<String, Object>> maps = reader.readAll();
        log.debug(">>>>>>>>>>>>>>>>>>>>>导入excel到PLM代码<<<<<<<<<<<<<<<<<<<<<");
        return WebResponse.success(ResCode.SUCCESS,maps);
    }
}
