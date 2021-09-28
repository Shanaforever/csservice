package com.centricsoftware.core.controller;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.centricsoftware.commons.dto.ResEntity;
import com.centricsoftware.commons.dto.WebResponse;
import com.centricsoftware.commons.em.ResCode;
import com.centricsoftware.config.cons.Constants;
import com.centricsoftware.config.entity.CsProperties;
import jcifs.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 简单的授权认证接口
 * @author zheng.gong
 * @date 2021/1/8
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class SimpleAuthController {

    final
    CsProperties csProperties;

    public SimpleAuthController(CsProperties csProperties) {
        this.csProperties = csProperties;
    }

    @GetMapping("/simpleAuthStr")
    public ResEntity getSimpleAuthString(String username,String password){
        if(Objects.equals(csProperties.getEnv().getProperty("web.auth.username"), username)
                && Objects.equals(csProperties.getEnv().getProperty("web.auth.password"), password)){
            String now = LocalDate.now().toString();
            byte[] decode = Base64.decode(Constants.SysStr.AUTH_ENCODE);
            AES aes = SecureUtil.aes(decode);
            //加密
            String encryptHex = aes.encryptHex(Constants.SysStr.AUTH_CONTENT+now);
            log.debug("加密后的字符串：{}",encryptHex);
            return WebResponse.success(ResCode.SUCCESS,encryptHex);
        }else{
            return WebResponse.failure(ResCode.AUTHORIZE_UP_ERROR);
        }
    }


}
