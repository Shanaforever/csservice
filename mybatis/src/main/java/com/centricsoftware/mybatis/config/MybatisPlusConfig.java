package com.centricsoftware.mybatis.config;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * mybatis-plus 配置
 * @author zheng.gong
 * @date 2020/4/20
 */
@Slf4j
@Configuration
@MapperScan("com.centricsoftware.mybatis.mapper")
@EnableTransactionManagement
public class MybatisPlusConfig {
    /**
     * 可定义多个数据源配置
     * @return
     */
    @Primary
    @Bean("sqlserverProperties")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties sqlServerDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 可定义多个数据源
     * @return DataSource
     */
    @Primary
    @Bean("sqlserver")
    public DataSource dataSource() {
        DataSourceProperties dataSourceProperties = this.sqlServerDataSourceProperties();
        return createDataSource(dataSourceProperties);
    }


    /**
     * 全局配置
     * @return GlobalConfig
     */
    @Bean
    public GlobalConfig globalConfiguration() {

        GlobalConfig.DbConfig conf = new GlobalConfig.DbConfig();

        /*
        AUTO|0:"数据库ID自增",
        NONE|1:"无",
        INPUT|2:"用户输入ID",
        ID_WORKER|3:"TwitterSnowflake方案（数字）"，
        UUID|4:"全局唯一ID UUID";
        ID_WORKER_STR|5:"TwitterSnowflake方案（字符串）";
        */
        //#主键类型
        conf.setIdType(IdType.AUTO);
        conf.setLogicDeleteValue("1");
        conf.setLogicNotDeleteValue("0");


//        conf.setKeyGenerator(oracleKeyGenerator());
        GlobalConfig globalConfig=new GlobalConfig();
//        globalConfig.setMetaObjectHandler(new MyMetaObjectHandler());

       /* globalConfig.setDatacenterId(sysProperties.getDataCenterId());
        globalConfig.setWorkerId(sysProperties.getWorkerId());*/
        globalConfig.setDbConfig(conf);
        globalConfig.setBanner(false);
        return globalConfig;
    }

//    @Bean
//    public OracleKeyGenerator oracleKeyGenerator(){
//        return new OracleKeyGenerator();
//    }


    /**
     * mybatis配置，具体参考：https://mp.baomidou.com/config/#mapunderscoretocamelcase
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Bean("sqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("sqlserver") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        //实体类扫码路径
        sqlSessionFactory.setTypeAliasesPackage("com.centricsoftware.**.mybatis.entity");
        sqlSessionFactory.setDataSource(dataSource);
        //mapper xml路径
        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "mapper/*.xml";
        sqlSessionFactory.setMapperLocations(pathMatchingResourcePatternResolver.getResources(packageSearchPath));


        MybatisConfiguration configuration = new MybatisConfiguration();
        //自动下划线转驼峰，否则自定义mapper注解@select 映射不到实体类上
        configuration.setMapUnderscoreToCamelCase(true);
        //configuration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        //配置JdbcTypeForNull, oracle数据库必须配置
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        //配置加载
        sqlSessionFactory.setConfiguration(configuration);
        //分页等拦截器
        sqlSessionFactory.setPlugins(//分页插件注册
                paginationInterceptor());
        sqlSessionFactory.setGlobalConfig(globalConfiguration());
        return sqlSessionFactory.getObject();
    }


    private HikariDataSource createDataSource(DataSourceProperties properties){
        // 创建 HikariDataSource 对象
        HikariDataSource dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
//        if(StrUtil.equals(env.getProperty("password.encrypt"),"true")){
//            String pwd = dataSource.getPassword();
//            // 密钥
//            byte[] key = Base64.decode(Constants.SysStr.SECRET);
//            AES aes = SecureUtil.aes(key);
//            // 解密
//            String password = aes.decryptStr(pwd, CharsetUtil.CHARSET_UTF_8);
//            dataSource.setPassword(password);
//        }
        // 设置线程池名
        if (StringUtils.hasText(properties.getName())) {
            dataSource.setPoolName(properties.getName());
        }
        return dataSource;
    }


    /**
     * 分页插件
     * 文档：http://mp.baomidou.com
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}

