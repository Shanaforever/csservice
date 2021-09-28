package com.centricsoftware.config.cons;


/**
 * 系统内相关常量,公共常量放这里，使用接口
 * @author zheng.gong
 * @date 2020/4/17
 */
public interface Constants {

    /**
     * 表名
     * @author zheng.gong
     * @date 2020/4/17
     */
    interface PlmTable {
        String PLM_SAMPLE="plm_sample";
    }

    interface Bool{
        String TRUE="true";
        String FALSE="false";
    }

    interface SysStr{
        String SESSION_EXPIRE_ERROR_MSG="The client session has expired or is invalid";
        String SECRET="pvKMVgcoYEtwnmqmWhJmaA==";
        String AUTH_CONTENT = "ICICLE_INTERFACE_AUTH";
        String AUTH_ENCODE = "beErF6b+D/CSC7k0TgVRNw==";
    }



    /**
     * 标点符号
     * @author zheng.gong
     * @date 2020/4/17
     */
    interface SysConts{
        /**
         * 点号
         */
        String SYMBOL_DOT = ".";

        /**
         * 星号
         */
        String SYMBOL_STAR = "*";

        /**
         * 邮箱符号
         */
        String SYMBOL_EMAIL = "@";



    }

    /**
     * http请求头
     * @author zheng.gong
     * @date 2020/4/17
     */
    interface HttpHeadEncodingType{
        String DEFAULT_CHARSET = "UTF-8";

        String CONTENT_TYPE_JSON="application/json;charset=UTF-8";

        String CONTENT_TYPE_XML="text/xml;charset=UTF-8";

        String CONTENT_TYPE_HTML="text/html;charset=utf-8";

        String CONTENT_TYPE_FORM="application/x-www-form-urlencoded";
    }

    /**
     * redis相关常量
     */
    interface Redis{
        /**
         * 默认配置刷新时间 7天
         */
        Integer DEFUALT_PROPERTIES_RELOAD = 604800;

        Integer C8INTERFACE_EXPIRE_TIME=3000;

    }

    /**
     * rabbit相关常量
     * 此类为service使用配置
     */
    interface RabbitConsts {
        /**
         * 直接模式1
         */
        String DIRECT_MODE_QUEUE_ONE = "queue.direct.1";

        /**
         * 队列2
         */
        String QUEUE_TWO = "queue.2";

        /**
         * 队列3
         */
        String QUEUE_THREE = "3.queue";

        /**
         * 分列模式
         */
        String FANOUT_MODE_QUEUE = "fanout.mode";

        /**
         * 主题模式
         */
        String TOPIC_MODE_QUEUE = "topic.mode";

        /**
         * 路由1
         */
        String TOPIC_ROUTING_KEY_ONE = "queue.#";

        /**
         * 路由2
         */
        String TOPIC_ROUTING_KEY_TWO = "*.queue";

        /**
         * 路由3
         */
        String TOPIC_ROUTING_KEY_THREE = "3.queue";

        /**
         * 延迟队列
         */
        String DELAY_QUEUE = "delay.queue";

        /**
         * 延迟队列交换器
         */
        String DELAY_MODE_QUEUE = "delay.mode";
    }


}
