package com.centricsoftware.core.filter;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.centricsoftware.commons.em.ResCode;
import com.centricsoftware.commons.exception.BaseException;
import com.centricsoftware.config.cons.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

/**
 * 添加一个简单的拦截器，用于校验token
 * 这个Demo比较简单，只是用于简单校验一下接口对接时是否是授权接口，其安全性并不足以应对外开放端口的请求攻击
 * @author zheng.gong
 * @date 2021/1/8
 */
//@Component
@Slf4j
public class SimpleAuthInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("=====================拦截请求===========================");
        String accessToken = request.getHeader("Authorization");
        log.debug("授权token：{}",accessToken);
        //简单认证不使用jwt认证模式
        byte[] decode = Base64.decode(Constants.SysStr.AUTH_ENCODE);
        AES aes = SecureUtil.aes(decode);
        try{
            String now = LocalDate.now().toString();
            //解密为字符串
            String decryptStr = aes.decryptStr(accessToken, CharsetUtil.CHARSET_UTF_8);
            log.debug("解密后的字符串：{}",decryptStr);
            Assert.isTrue(decryptStr.equals(Constants.SysStr.AUTH_CONTENT+now),"请求未授权！");
        }catch (Exception e){
            throw new BaseException(ResCode.AUTHORIZE_ERROR);
        }
        return true;
    }
}
