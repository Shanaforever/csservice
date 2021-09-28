package com.centricsoftware.core.strategyservice.impl;

import com.centricsoftware.config.entity.CenterProperties;
import com.centricsoftware.core.strategyservice.AbstractDemoStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
/**
 * 测试转发类1-native export导出
 * 该类将通过{@link com.centricsoftware.core.controller.DispatchController} 中的type找到
 * @author zheng.gong
 * @date 2020/4/21
 */
@Slf4j
@Service("DemoStrategyService1Impl")
public class AbstractDemoStrategyService1Impl extends AbstractDemoStrategyService {
    final
    CenterProperties properties;

    public AbstractDemoStrategyService1Impl(CenterProperties properties) {
        this.properties = properties;
    }

//    @Override
//    public ResEntity process(Map<String, String> params) {
//        log.debug("--------------------DemoStrategyService1    Impl---------------------------");
//        Map<Object, Object> connMap = getConn();
//        boolean iscon = (boolean)connMap.get("iscon");
//        Connection conn = (Connection)connMap.get("conn");
//        if(!iscon){
//            //链接失败
//        }
//        NodeUrl nurl = new NodeUrl(conn.q);
//        String styleUrl = "C1010";
//        ArrayList<HashMap<String, String>> list = new ArrayList();
//        HashMap<String, String> conmap = new HashMap();
////        conmap.put("path", "Child:__Parent__");
////        conmap.put("key", "__Parent__");
//        conmap.put("value", styleUrl);
//        conmap.put("type", "url");
//        list.add(conmap);
//
//        String notstr = properties.getValue("export.style.query", "");
//        String order = properties.getValue("export.style.order", "");
//        ArrayList<String> queryXMList = NodeUrl.getQueryXML("Style", list, notstr, order);
//        VerticalExtract verticalExtract = new VerticalExtract(conn.q);
//        LinkedHashMap<String, ArrayList<String>> rMap = new LinkedHashMap();
//        for (String queryXML : queryXMList) {
//            log.info("queryXML：{}" , queryXML);
//            ArrayList<HashMap<String, String>> exlist = new ArrayList();
//            String special = properties.getValue("export.style.detail", "");
//            String extractXML = NodeUrl.getExtractXML(exlist, special);
//            log.info("extractXML=" + extractXML);
//            ResultSet rs = verticalExtract.getData(queryXML, extractXML, "");
//
//            LinkedHashMap<String, ArrayList<String>> aMap = nurl.getDataResultMap(rs,"export.style.");
//            rMap.putAll(aMap);
//            // aMap.clear();
//            aMap = null;
//
//        }
//        rMap.forEach((k,v)-> System.out.println(k+"---"+v));
//        //TODO 处理数据
//        return WebResponse.success(ResCode.SUCCESS, rMap);
//    }
//
//    /**
//     * 获取数据库链接
//     * @return 数据库链接
//     */
//    public Map<Object, Object> getConn(){
//        Connection conn = new Connection();
//        String dbhost = csProperties.getValue("dbhost");
//        String dbuser = csProperties.getValue("dbuser");
//        String dbpwd = csProperties.getValue("dbpwd");
//        String dbname = csProperties.getValue("dbname");
//        String dbType = csProperties.getValue("dbtype");
//        boolean iscon = conn.connectDb(dbhost, dbname, dbuser, dbpwd, dbType);
//
//        return MapUtil.builder().put("iscon", iscon).put("conn", conn).build();
//    }


}
