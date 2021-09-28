package com.centricsoftware.config.entity;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.sql.Struct;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "cs.center")
public class CenterProperties {
    Map<String, String> prop;

    public String getValue(String key, String defValue){
        String value = this.getProp().get(key);
        if(StrUtil.isBlank(value)&&StrUtil.isNotBlank(defValue)){
            return defValue;
        }else{
            return value;
        }
    }

    public String getValue(String key){
        return this.getProp().get(key);
    }
}
