package com.centricsoftware.core.strategyservice.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import com.centricsoftware.commons.dto.ResEntity;
import com.centricsoftware.commons.dto.WebResponse;
import com.centricsoftware.commons.em.ResCode;
import com.centricsoftware.commons.exception.C8UnsupportedEncodingException;
import com.centricsoftware.commons.utils.NodeUtil;
import com.centricsoftware.config.entity.CenterProperties;
import com.centricsoftware.core.strategyfactory.BaseCtxStrategyService;
import com.centricsoftware.core.strategyservice.AbstractDemoStrategyService;
import com.centricsoftware.pi.tools.util.C8ResponseXML;
import com.centricsoftware.pi.tools.xml.Document;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 测试转发类 restapi导出
 * 该类将通过{@link com.centricsoftware.core.controller.DispatchController} 中的type找到
 * 数据导出接口实现process
 * 配置在 application-center.yml,application-custom中，所有配置的配置声明必须是cs.center.prop，后面为configName,然后是具体配置信息
 * 在同一个yml文件中不需要多个配置声明
 * #款式和颜色库导出
 * cs.center.prop:
 *   colorspec:
 *     url: http://127.0.0.1:8777/demo/hello
 *     count: 2
 *     1:
 *       name: Code
 *       value: Code
 *     2:
 *       name: Description
 *       value: Description
 * cs.center.prop:
 *   style:
 *     url: http://127.0.0.1:8777/demo/hello
 *     1:
 *       name: Code
 *       value: Code
 *     2:
 *       name: Description
 *       value: Description
 * @author zheng.gong
 * @date 2020/4/21
 */
@Slf4j
@Service("demoStrategyServiceImpl")
public class AbstractDemoStrategyServiceImpl extends AbstractDemoStrategyService{
    @Autowired
    CenterProperties centerProperties;

    /**
     * 导出数据的接口，实现数据取值
     */
    @Override
    public ResEntity process(Map<String, String> params) throws Exception{
        log.debug("--------------------DemoStrategyService    Impl---------------------------");
        //获取configName
        String configName = params.get("configName");
        //1.登陆将交由系统自动完成
        log.info("开始执行导出基本数据信息");
        //2. 数据查询
        String queryXML = "";
        queryXML += "<Node Parameter=\"Type\" Op=\"EQ\" Value=\"Style\" />";
        log.info("查询语句:{}",queryXML);
        String count = Optional.ofNullable(centerProperties.getValue(configName+".count","50"))
                .orElseThrow(()-> new C8UnsupportedEncodingException(ResCode.PARAM_NOT_MATCH));
        if(count==null ){
            throw new C8UnsupportedEncodingException(ResCode.PARAM_NOT_MATCH);
        }
        int intCount = Integer.parseInt(count);
        Document doc = NodeUtil.queryByXMLMinWithCount(queryXML, intCount);
        List<String> resultURLs = C8ResponseXML.resultNodeCNLs(doc);
        log.info("查询结果:{}",resultURLs.size());
        //3.数据处理
        int countNum = 1;
        for (String url : resultURLs) {
            log.info("count={}/{}",countNum++,resultURLs.size());
            log.info("url={}",url);
            //转换JSON格式
            JSONObject jsObject = new JSONObject();
            HashMap<String,String> nodeMap = NodeUtil.queryAttributes(url);
            jsObject = BaseCtxStrategyService.getJSONObjectFromProperties(configName,jsObject,url,nodeMap);

            log.info("json={}",jsObject.toString());
            String msg = HttpUtil.post(centerProperties.getValue(configName+".url"), jsObject.toString());
            log.info("msg={}",msg );
            msg = msg.toLowerCase();
            if(msg.contains("success")){
                String updateXML = "";
                updateXML += "<ChangeNode URL=\""+url+"\" >\n";
                updateXML += "    <ChangeAttribute Id=\"Description\" Type=\"string\" Value=\""+url+"更新成功\" />\n";
                updateXML += "</ChangeNode>\n";
                NodeUtil.processNode(updateXML);
            }else{
                String updateXML = "";
                updateXML += "<ChangeNode URL=\""+url+"\" >\n";
                updateXML += "    <ChangeAttribute Id=\"Description\" Type=\"string\" Value=\""+msg+"\" />\n";
                updateXML += "</ChangeNode>\n";
                NodeUtil.processNode(updateXML);
            }

        }
        log.info("处理结束");
        log.info("");
        return WebResponse.success(ResCode.SUCCESS, params);
    }




}
