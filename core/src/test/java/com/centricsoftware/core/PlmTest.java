package com.centricsoftware.core;

import cn.hutool.core.lang.Console;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.centricsoftware.commons.utils.NodeUrl;
import com.centricsoftware.commons.utils.NodeUtil;
import com.centricsoftware.commons.utils.RestAPIUtil;
import com.centricsoftware.commons.utils.SpringUtil;
import com.centricsoftware.config.entity.CenterProperties;
import com.centricsoftware.config.entity.CsProperties;
import com.centricsoftware.core.strategyservice.impl.AbstractDemoStrategyServiceImpl;
import com.centricsoftware.pi.export.importer2.c8.Connection;
import com.centricsoftware.pi.export.query.VerticalExtract;
import com.centricsoftware.pi.tools.http.C8Communication;
import com.centricsoftware.pi.tools.http.ConnectionInfo;
import com.centricsoftware.pi.tools.util.C8ResponseXML;
import com.centricsoftware.pi.tools.xml.Document;
import com.centricsoftware.pi.tools.xml.Element;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.sql.ResultSet;
import java.util.*;

@Slf4j
@ComponentScan("com.centricsoftware")
@SpringBootTest
public class PlmTest {
    @Autowired
    CenterProperties centerProperties;

    @Autowired
    AbstractDemoStrategyServiceImpl abstractDemoStrategyService;
    @Test
    public void test(){
        ConnectionInfo info = new ConnectionInfo("192.168.30.129", "Administrator", "c8admin");
        try {
            //获得cookie
            HttpURLConnection conn = (HttpURLConnection)info.getRequestURL().openConnection();
            Map<String, List<String>> map = CookieHandler.getDefault().get(info.getRequestURL().toURI(), conn.getHeaderFields());
            List<String> cookies = map.get("Cookie");
//            cookies.forEach(s-> System.out.println("cookie:"+s));
            for (int i = 0; i < cookies.size(); i++) {
                String cookie = cookies.get(i);
                String[] cookieSplit = StrUtil.split(cookie, ";");
                for (int j = 0; j < cookieSplit.length; j++) {
                    String cookieVaraStr = cookieSplit[j];
                    String[] cookieStr = StrUtil.split(cookieVaraStr, "=");
                    System.out.println(cookieStr.length);
                }
                System.out.println(cookie);
            }
            //查询c8
            String xml="<Node Parameter=\"Type\" Op=\"EQ\" Value=\"User\" />";
            StringBuffer operationXML = new StringBuffer();
            LinkedList parameters = new LinkedList();
            if (xml != null && !"".equals(xml) && !"null".equalsIgnoreCase(xml)) {
                operationXML.append("<?xml version='1.0' encoding='utf-8' ?>\n<Query>\n");
                operationXML.append(xml + "\n");
                operationXML.append("</Query>\n");
                parameters.add(new C8Communication.Param(false, "Module", "Search"));
                parameters.add(new C8Communication.Param(false, "Operation", "QueryByXML"));
                parameters.add(new C8Communication.Param(false, "Qry.XML", operationXML.toString()));

                Document doc = C8Communication.post(info, parameters, true);
                List<String> list = C8ResponseXML.resultNodeCNLs(doc);
                list.forEach(System.out::println);
            }




        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() throws Exception {
//        Document document = NodeUtil.queryByXML("<Node Parameter=\"Type\" Op=\"EQ\" Value=\"Style\" />\"");
//        List<String> list = C8ResponseXML.resultNodeCNLs(document);
//        list.forEach(System.out::println);

        String samList = NodeUtil.querySingleAttrByUrlList("C111719", "C8_Style_Samples").toString();
        Console.log("samList: {}",samList);


    }

    @Test
    public void test2(){
        Map<String, String> prop = centerProperties.getProp();
        prop.forEach((k,v)-> System.out.println(k+"----"+v));

    }

    /**
     * 批量导出数据样例。配置文件：application-center.yml
     */
    @Test
    public void test3() throws Exception{
        Map<String, String> map =
                MapUtil.builder("configName", "colorspec").build();
        abstractDemoStrategyService.process(map);
    }
    /**
     * 批量导出数据样例。配置文件：application-custom.yml
     */
    @Test
    public void test4() throws Exception{
        Map<String, String> map =
                MapUtil.builder("configName", "style").build();
        abstractDemoStrategyService.process(map);
    }

    /**
     * 测试数据库链接
     */
    @Test
    public void test5(){
        CenterProperties properties = SpringUtil.getBean(CenterProperties.class);
        Map<Object, Object> connMap = getConn();
        boolean iscon = (boolean)connMap.get("iscon");
        Connection conn = (Connection)connMap.get("conn");
        if(!iscon){
            //链接失败
        }
        NodeUrl nurl = new NodeUrl(conn.q);
        String styleUrl = "C1010";
        ArrayList<HashMap<String, String>> list = new ArrayList();
        HashMap<String, String> conmap = new HashMap();
//        conmap.put("path", "Child:__Parent__");
//        conmap.put("key", "__Parent__");
        conmap.put("value", styleUrl);
        conmap.put("type", "url");
        list.add(conmap);

        String notstr = properties.getValue("export.style.query", "");
        String order = properties.getValue("export.style.order", "");
        ArrayList<String> queryXMList = NodeUrl.getQueryXML("Style", list, notstr, order);
        VerticalExtract verticalExtract = new VerticalExtract(conn.q);
        LinkedHashMap<String, ArrayList<String>> rMap = new LinkedHashMap();
        for (String queryXML : queryXMList) {
            log.info("queryXML：{}" , queryXML);
            ArrayList<HashMap<String, String>> exlist = new ArrayList();
            String special = properties.getValue("export.style.detail", "");
            String extractXML = NodeUrl.getExtractXML(exlist, special);
            log.info("extractXML=" + extractXML);
            ResultSet rs = verticalExtract.getData(queryXML, extractXML, "");

            LinkedHashMap<String, ArrayList<String>> aMap = nurl.getDataResultMap(rs,"export.style.");
            rMap.putAll(aMap);
            // aMap.clear();
            aMap = null;

        }
        rMap.forEach((k,v)-> System.out.println(k+"---"+v));

    }

    @Test
    public void test10(){
        Map<Object, Object> connMap = getConn();
        boolean iscon = (boolean)connMap.get("iscon");
        Connection conn = (Connection)connMap.get("conn");
        if(!iscon){
            //链接失败
        }
        NodeUrl nurl = new NodeUrl(conn.q);
        String query="<QUERY><AND><Predicate NodeType=\"Style\" Operand=\"EQ\" />" +
                "</AND></QUERY>";
        String extractXML="<EXTRACT><DETAIL AttributeName=\"Node Name\" Seq=\"1\"/><DETAIL AttributeName=\"Code\" Seq=\"2\" /></EXTRACT>";
        String queryXML=String.format(query,"true");
        VerticalExtract verticalExtract = new VerticalExtract(conn.q);
        // Query query = new Query(conn.q);
        ResultSet rs = verticalExtract.getData(queryXML, extractXML, "");// conn.q.getData(queryXML, // extractXML);
        LinkedHashMap<String, HashMap<String, String>> rMap = nurl.getDataResultMapAttr(rs, "export.style.",
                NodeUrl.DBQUERY);
//        sumMap.putAll(rMap);


    }

    @Test
    public void test11() throws Exception{
        Element element = NodeUtil.queryElementbyUri("C1502");
        Object nodeName = NodeUtil.querySingleAttribute(element, "Node Name", false);
        Object code = NodeUtil.querySingleAttribute(element, "Code", false);
        Object description = NodeUtil.querySingleAttribute(element, "Description", false);
        Console.log("Node Name:{},code:{},descrioption:{}",nodeName,code,description);
    }

    public Map<Object, Object> getConn(){
        CsProperties properties = NodeUrl.properties;
        Connection conn = new Connection();
        String dbhost = properties.getValue("dbhost");
        String dbuser = properties.getValue("dbuser");
        String dbpwd = properties.getValue("dbpwd","CSIDBA");
        String dbname = properties.getValue("dbname");
        String dbType = properties.getValue("dbtype");
        boolean iscon = conn.connectDb(dbhost, dbname, dbuser, dbpwd, dbType);
        Map<Object, Object> map = MapUtil.builder().put("iscon", iscon).put("conn", conn).build();
        return map;
    }

    /**
     * 测试excel转pdf
     */
    @Test
    public void test6(){
//        ExcelUtil.excel2pdf("D:\\ideaworkspace\\exceltopdf\\src\\test\\resources\\com\\github\\caryyu\\excel2pdf" +
//                "\\sample1\\ProductBrief.xlsx","D:\\test\\generate" +
//                "\\test.pdf");
    }

    /**
     * c8官方uri double encoding 方法
     */
    @Test
    public void test7() {
//        String arg = "centric://APPAREL/Collection/Etirel_Youth";
//        String encodeString = RestAPIUtil.doubleEncodeString(arg);
//        System.out.println(encodeString);

    }

    /**
     * 测试cookie
     */
    @Test
    public void test8(){
//        String cookie = NodeUtil.getCookie();
//        System.out.println(cookie);
//        String restUrl = "http://192.168.30.176/csi-requesthandler/api/v2/styles?skip=0&limit=10";
//        String body =
//                HttpUtil.createGet(restUrl).header("Content-Type", "application/json").header("cookie", cookie).execute().body();
//        System.out.println(body);
    }

    /**
     * rest api test
     */
    @Test
    public void test9() throws Exception {
        String res = RestAPIUtil.excute("styles/C1502", "", "get");
        Console.log("result:{}", JSONUtil.parseObj(res));
    }


}
