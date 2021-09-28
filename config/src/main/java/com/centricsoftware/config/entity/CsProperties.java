package com.centricsoftware.config.entity;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "cs")
public class CsProperties {
    Map<String,String> plm;

    private Environment env;

    @Autowired
    public CsProperties(Environment env) {
        this.env = env;
    }

    public String getValue(String key, String defValue){
        String value = this.getPlm().get(key);
        if(StrUtil.isBlank(value)&&StrUtil.isNotBlank(defValue)){
            return defValue;
        }else{
//            if(StrUtil.equals(env.getProperty("password.encrypt"),"true")){
//                // 密钥
//                byte[] encryptkey = Base64.decode(Constants.SysStr.SECRET);
//                AES aes = SecureUtil.aes(encryptkey);
//                // 解密
//                return aes.decryptStr(value, CharsetUtil.CHARSET_UTF_8);
//            }
            return value;
        }
    }

    public String getValue(String key){
        return this.getPlm().get(key);
    }
    public String getProperty(String key){
        return this.getEnv().getProperty(key);
    }
    public String getProperty(String key, String defValue){
        String value = this.getEnv().getProperty(key);
        if(StrUtil.isBlank(value)) {
            value = defValue;
        }
        return value;
    }

}
