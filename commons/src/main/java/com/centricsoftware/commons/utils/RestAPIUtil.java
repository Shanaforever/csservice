package com.centricsoftware.commons.utils;

import cn.hutool.core.lang.Console;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.centricsoftware.config.entity.CsProperties;
import com.centricsoftware.pi.tools.http.ConnectionInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * C8 RestAPI查询
 *
 * @author zheng.gong
 * @date 2020/5/12
 */
@Slf4j
public class RestAPIUtil {
    static ConnectionInfo info;

    static {
        info = NodeUtil.getConnection();
    }

    /**
     * 调用Rest API的方法
     *
     * @param url       传入的Rest API
     * @param paramters 参数值
     * @param type      执行的方式
     * @return
     * @throws Exception
     */
    public static String excute(String url, String paramters, String type) throws Exception {
        CsProperties csProperties = NodeUtil.getProperties();
        // 根据传入的BO对象的type去获取需要的请求连接
        String C8URL = csProperties.getProperty("cs.plm.rest.host") + url;
        Console.log("request url:{}",C8URL);
        String result = "";
        if (NodeUtil.checkSession()) {
            log.info("尝试重新登陆");
            boolean b = NodeUtil.reLogin(info);
            if (b) {
                log.info("重新登陆成功！");
                excute(url, paramters, type);
            }
        } else {
            switch (type) {
                case "get":
                    result = HttpUtil.get(C8URL);
                    break;
                case "post":
                    result = HttpUtil.post(C8URL, paramters);
                    break;
                case "put":
                    HttpRequest put = HttpUtil.createRequest(Method.PUT, C8URL);
                    // 设置实体数据
                    result = put.body(paramters).execute().body();
                    break;
                case "delete":
                    HttpRequest delete = HttpUtil.createRequest(Method.DELETE, C8URL);
                    result = delete.body(paramters).execute().body();
                    break;
            }
            log.info(result);
        }
        return result;
    }

    /**
     * 对uri进行双编码
     *
     * @param strToEncode 需要进行编码的uri
     * @return 编码后的uri
     */
    public static String doubleEncodeString(String strToEncode) {
        String step1;
        String step2;
        try {
            step1 = URLEncoder.encode(strToEncode, "UTF-8");
            if (step1.equals(strToEncode)) {
                System.err.println("Input arg: '" + strToEncode + "' does not require encoding.");
                return null;
            }
            step2 = URLEncoder.encode(step1, "UTF-8");
            System.out.println("Input arg: '" + strToEncode + "' first encode: '" +
                    step1 + "', second encode: '" + step2 + "'.");
            return step2;
        } catch (UnsupportedEncodingException uee) {
            //throw new C8UnsupportedEncodingException(ResCode.C8_UNSUPPORTED_ENCODING_ERROR,"UnsupportedEncodingException: " + uee + " attempting to encode argument " + strToEncode + ".");
            System.out.println(uee);
        }
        return "";
    }
}
