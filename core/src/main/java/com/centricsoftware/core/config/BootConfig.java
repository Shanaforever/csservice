package com.centricsoftware.core.config;

import cn.hutool.core.util.StrUtil;
import com.centricsoftware.commons.utils.NodeUtil;
import com.centricsoftware.config.cons.Constants;
import com.centricsoftware.config.entity.CsProperties;
import com.centricsoftware.pi.tools.http.C8Communication;
import com.centricsoftware.pi.tools.http.ConnectionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

/**
 * 包含一些启动后需要注册的信息
 * @author zheng.gong
 * @date 2020/4/21
 */
@Slf4j
@Configuration
public class BootConfig {

    /**
     * 按照标准时间来算，晚上12点，中午12点执行登陆
     */
    @Scheduled(cron = "${task.cron}")
    public void job1() {
        log.info("【重新登陆】"+LocalDateTime.now());
        ConnectionInfo info = NodeUtil.getConnection();
        NodeUtil.reLogin(info);
    }

    /**
     * 启动后配置
     * @return LoadingConfig
     */
    @Bean
    public LoadingConfig cfg(){
        return new LoadingConfig();
    }

    /**
     * 启动后的初始化操作
     * @author zheng.gong
     * @date 2020/4/23
     */
    static class LoadingConfig{
        @Autowired
        CsProperties csProperties;
        /**
         * 启动后自动登陆PLM
         */
        @PostConstruct
        public void init(){
            ConnectionInfo info = NodeUtil.getConnection();
            if(StrUtil.equals(csProperties.getValue("auto-login"), Constants.Bool.TRUE,true) || StrUtil.isBlank(csProperties.getValue(
                    "auto-login"))){
                try {
                    log.debug("===========系统启动后登陆PLM============");
                    C8Communication.simpleLogin(info);
                    log.debug("=========登陆成功！=========");
                    NodeUtil.initEnumResource();
                } catch (Exception e) {
                    log.error("c8 login error",e);
                }
            }else{
                log.debug("===========系统启动后不登陆PLM============");
            }
        }
    }


}
