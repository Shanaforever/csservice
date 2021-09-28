package com.centricsoftware.commons.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.centricsoftware.commons.dto.DepPath;
import com.centricsoftware.commons.dto.DepPathResult;
import com.centricsoftware.config.cons.Constants;
import com.centricsoftware.config.entity.CsProperties;
import com.centricsoftware.pi.export.importer2.c8.Connection;
import com.centricsoftware.pi.export.query.C8Connection;
import com.centricsoftware.pi.export.query.ClientHttpRequest;
import com.centricsoftware.pi.tools.http.C8Communication;
import com.centricsoftware.pi.tools.http.ConnectionInfo;
import com.centricsoftware.pi.tools.util.C8ResponseXML;
import com.centricsoftware.pi.tools.xml.Document;
import com.centricsoftware.pi.tools.xml.Element;
import com.centricsoftware.pi.tools.xml.XML;
import com.centricsoftware.pi.tools.xml.XPath;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * C8 API
 * @author zheng.gong
 * @date 2020/4/23
 */
@Slf4j
public class NodeUtil {
    private static String api;
    static ConnectionInfo info;
    public static HashMap<String, String> enLocaleMap = new HashMap<>();

    public static HashMap<String, String> zhLocaleMap = new HashMap<>();
    static{
        CsProperties properties = getProperties();
        info = getConnection();
        api = info.getProtocol()+"://"+ info.getServer() + "/csi-requesthandler/RequestHandler";
    }
    /**
     * 最小化查询，查询结果中只返回Node Name和URL，查询效率高
     *
     * @param xml
     * @return
     */
    public static Document queryByXMLMin(String xml) throws Exception {
        StringBuffer operationXML = new StringBuffer();
        LinkedList parameters = new LinkedList();
        try {
            System.out.println("begin to query node:\n" + xml);
            if (xml != null && !"".equals(xml) && !"null".equalsIgnoreCase(xml)) {
                operationXML.append("<?xml version='1.0' encoding='utf-8' ?>\n<Query>\n");
                operationXML.append(xml + "\n");
                operationXML.append("</Query>\n");
                parameters.add(new C8Communication.Param(false, "Module", "Search"));
                parameters.add(new C8Communication.Param(false, "Operation", "QueryByXML"));
                parameters.add(new C8Communication.Param(false, "Fmt.Attr.Inc", "No"));
                parameters.add(new C8Communication.Param(false, "Fmt.Node.Info", "Min"));
                parameters.add(new C8Communication.Param(false, "Qry.XML", operationXML.toString()));

                return C8Communication.post(info, parameters, true);
            }

        } catch (Exception e) {
            if(checkSession()){
                log.error("NodeUtil执行失败，失败方法：processNode,错误信息：{}",e.getMessage());
                log.info("尝试重新登陆");
                boolean b = reLogin(info);
                if(b){
                    log.info("重新登陆成功！");
                    return queryByXMLMin(xml);
                }
            }else{
                log.error("NodeUtil执行失败，失败方法：processNode,错误信息：{}",e.getMessage(),e);
                throw e;
            }
        }
        return new Document();
    }
    /**
     * 发布文档，生成cf url
     * @param fileName
     * @param fileUrl
     * @return
     */
    public static String publishFile(String fileName, String fileUrl) {
        CsProperties properties = getProperties();
        String serverHost = properties.getPlm().get("serverhost");
        String user = properties.getPlm().get("user");
        String pwd = properties.getPlm().get("pwd");
        C8Connection c8 = new C8Connection();

        c8.login(serverHost, user, pwd);
        if (fileUrl == null) {
            fileUrl = "";
        }
        return c8.publishFile(fileName, fileUrl);

    }

    /**
     * 获取配置信息
     * @return CsProperties
     */
    public static CsProperties getProperties(){
        return SpringUtil.getBean(CsProperties.class);
    }

    public static boolean checkSession() throws Exception{
        ConnectionInfo info = NodeUtil.getConnection();
        LinkedList<C8Communication.Param> parameters = new LinkedList();
        parameters.add(new C8Communication.Param(false, "Module", "Search"));
        parameters.add(new C8Communication.Param(false, "Operation", "QueryByURL"));
        parameters.add(new C8Communication.Param(false, "Qry.URL", "_CS_Data"));
        InputStream in = C8Communication.post(info, parameters);
        Document doc = XML.fromStream(in);
        Element response = doc.getRootElement();
        Element adminError = response.element("ErrorAdmin");
        String sAdminError = adminError.getText();
        return sAdminError.contains(Constants.SysStr.SESSION_EXPIRE_ERROR_MSG);
    }
    /**
     * 获取连接信息
     * @return ConnectionInfo
     */
    public static ConnectionInfo getConnection(){
        CsProperties properties = getProperties();
        String serverHost = properties.getPlm().get("serverhost");
        String user = properties.getPlm().get("user");
        String pwd = properties.getPlm().get("pwd");
        return new ConnectionInfo(serverHost,user,pwd);
    }

    /**
     * 获取cookie
     * @return String
     */
    public static String getCookie(){
        //获得cookie
        HttpURLConnection conn;
        Map<String, List<String>> map = null;
        try {
            conn = (HttpURLConnection)info.getRequestURL().openConnection();
            map = CookieHandler.getDefault().get(info.getRequestURL().toURI(), conn.getHeaderFields());
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
        List<String> cookies = map.get("Cookie");
        return cookies.get(0);
    }

    /**
     * 通过File url来获取对应文件的流
     * @param curl curl
     * @return InputStream
     * @throws IOException e
     */
    public static InputStream getInputStream(String curl) throws IOException {
        InputStream serverOutput;
        String url = info.getProtocol()+"://"+ info.getServer();
        url = url + "/csi-requesthandler/RequestHandler?";
        url = url + "URL=" + curl;
        url = url + "&Module=Publisher";
        url = url + "&Attribute=Viewable";
        url = url + "&Operation=GetDirect";
        ClientHttpRequest session = new ClientHttpRequest(url);
        serverOutput = session.post();
        return serverOutput;
    }
    /**
     * 通过图片地址获取输入流
     * @param curl
     * @return
     */
    public static InputStream getInputStreamByDirect(String curl) {
        InputStream serverOutput = null;
        try {
            String url = info.getRequestURL().toString();
            url = url + "?";
            url = url + "URL=" + curl;
            url = url + "&Module=Publisher";
            url = url + "&Attribute=Viewable";
            url = url + "&Operation=GetDirect";
            ClientHttpRequest session = new ClientHttpRequest(url);
            serverOutput = session.post();
        } catch (Exception e) {
            log.error("文件流读取失败", e);
        }
        return serverOutput;
    }
    /**
     * 获取C8数据库连接
     * @return conn
     * @throws Exception e
     */
    public static Connection getDBConn() throws Exception {
        Connection conn = new Connection();
        CsProperties properties = getProperties();
        Map<String, String> plmProp = properties.getPlm();
        conn.connectDb(plmProp.get("dbhost"), plmProp.get("dbname"), plmProp.get("dbuser"), plmProp.get("dbpwd"), plmProp.get("dbtype"));
        return conn;
    }

    /**
     *
     * @param uri
     * @return
     * @throws Exception
     */
    public static InputStream getInputStreamByURi(String uri,String size) throws Exception{
        InputStream serverOutput = null;
        try {
            String url = "http://" + info.getServer();
            url = url + "/csi-requesthandler/RequestHandler?";
            url = url + "URL=" + uri;
            url = url + "&Module=Publisher";
            url = url + "&Attribute="+size;
            url = url + "&Operation=GetFromNode";

            ClientHttpRequest session = new ClientHttpRequest(url);
            serverOutput = session.post();
        } catch (Exception e) {
            if(checkSession()){
                log.error("NodeUtil执行失败，失败方法：processNode,错误信息：{}",e.getMessage());
                log.info("尝试重新登陆");
                boolean b = reLogin(info);
                if(b){
                    log.info("重新登陆成功！");
                    return getInputStreamByURi(uri,size);
                }
            }else{
                log.error("NodeUtil执行失败，失败方法：processNode,错误信息：{}",e.getMessage(),e);
                throw e;
            }

        }
        return serverOutput;
    }
    /**
     * 获取特定语言的翻译值
     *
     * @param language
     * @return
     */
    public static HashMap<String, String> getDisplayValueMap(String language) {
        HashMap<String, String> localMap = new HashMap<>();
        try{
            String xml = "<Node Parameter=\"Type\" Op=\"EQ\" Value=\"LocaleConfiguration\" />"
                    + "<Attribute Id=\"Node Name\" Op=\"EQ\" SValue=\"" + language + "\" />";
            Document doc = queryByXML(xml);
            if (doc != null) {
                List<String> resultsCNL = C8ResponseXML.resultNodeCNLs(doc);
                if (resultsCNL.size() > 0) {
                    String localCNL = resultsCNL.get(0);
                    Element root = C8ResponseXML.elementWithCNL(doc, localCNL);
                    for (Element element : root.elements()) {
                        String ename = element.attributeValue("Id");
                        String etype = element.attributeValue("Type");
                        String evalue = element.attributeValue("Value");
                        if ("Resources".equalsIgnoreCase(ename)) {
                            for (Element keyElement : element.elements()) {
                                String keyName = keyElement.attributeValue("Key");
                                String keyValue = keyElement.getText();
                                localMap.put(keyName, keyValue);
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return localMap;
    }
    /**
     * 创建一个对象URL并返回
     *
     */
    public static String createURL() throws Exception{
        String url = "";
        LinkedList<C8Communication.Param> parameters = new LinkedList<>();
        try {
            System.out.println("begin to create node url");

            parameters.add(new C8Communication.Param(false, "Module", "NodeProcessor"));
            parameters.add(new C8Communication.Param(false, "Operation", "CreateNodeURL"));
            parameters.add(new C8Communication.Param(false, "Count", 1 + ""));

            Document doc = C8Communication.post(info, parameters, true);
            List<Element> el = doc.getRootElement().element("URLS").elements();
            if (el.size() >= 1) {
                Element eel = el.get(0);
                url = eel.getText();
            }

        } catch (Exception e) {
            if(checkSession()){
                log.error("NodeUtil执行失败，失败方法：createURL,错误信息：{}",e.getMessage());
                log.info("尝试重新登陆");
                boolean b = reLogin(info);
                if(b){
                    log.info("重新登陆成功！");
                    return createURL();
                }
            }else{
                log.error("NodeUtil执行失败，失败方法：createURL,错误信息：{}",e.getMessage(),e);
                throw e;
            }
        }

        return url;
    }
    /**
     * 执行operation xml
     *
     * @param xml
     *            要执行的xml语句
     * @return success/fail
     */
    public static String processNode(String xml) throws Exception{
        String message = "SUCCESS";
        LinkedList<C8Communication.Param> parameters = new LinkedList<>();
        StringBuffer operationXML = new StringBuffer();
        try {
            log.info("---->begin to process node:\n" + xml);
            operationXML.append("<NODES_OPERATIONS>");
            operationXML.append(xml);
            operationXML.append("</NODES_OPERATIONS>");
            parameters.add(new C8Communication.Param(false, "Module", "NodeProcessor"));
            parameters.add(new C8Communication.Param(false, "Operation", "Execute"));
            parameters.add(new C8Communication.Param(false, "UpdatedNodes", operationXML.toString()));
            parameters.add(new C8Communication.Param(false, "Operation", "Execute"));
            parameters.add(new C8Communication.Param(false, "ReturnCNLs", "None"));
            parameters.add(new C8Communication.Param(false, "ReturnNodes", "None"));

            C8Communication.post(info, parameters, true);
        } catch (Exception e) {
            if(checkSession()){
                log.error("NodeUtil执行失败，失败方法：processNode,错误信息：{}",e.getMessage());
                log.info("尝试重新登陆");
                boolean b = reLogin(info);
                if(b){
                    log.info("重新登陆成功！");
                    return processNode(xml);
                }
            }else{
                log.error("NodeUtil执行失败，失败方法：processNode,错误信息：{}",e.getMessage(),e);
                throw e;
            }
        }
        return message;
    }
    /**
     * 根据XML进行查询，并返回所查询到对象的所有属性信息，
     *
     * @param xml
     * @return
     */
    public static Document queryByXML(String xml) throws Exception{
        StringBuffer operationXML = new StringBuffer();
        LinkedList parameters = new LinkedList();
        try {
            if (xml != null && !"".equals(xml) && !"null".equalsIgnoreCase(xml)) {
                operationXML.append("<?xml version='1.0' encoding='utf-8' ?>\n<Query>\n");
                operationXML.append(xml + "\n");
                operationXML.append("</Query>\n");
                parameters.add(new C8Communication.Param(false, "Module", "Search"));
                parameters.add(new C8Communication.Param(false, "Operation", "QueryByXML"));
                parameters.add(new C8Communication.Param(false, "Qry.XML", operationXML.toString()));

                return C8Communication.post(info, parameters, true);
            }
        } catch (Exception e) {
            if(checkSession()){
                log.error("NodeUtil执行失败，失败方法：queryByXML,错误信息：{}",e.getMessage());
                log.info("尝试重新登陆");
                boolean b = reLogin(info);
                if(b){
                    log.info("重新登陆成功！");
                    return queryByXML(xml);
                }
            }else{
                log.error("NodeUtil执行失败，失败方法：queryByXML,错误信息：{}",e.getMessage(),e);
                throw e;
            }

        }
        return new Document();
    }

    public static boolean reLogin(ConnectionInfo info){
        try {
            C8Communication.simpleLogin(info);
            initEnumResource();
            return true;
        } catch (Exception ex) {
            log.error("尝试重新登陆失败",ex);
            return false;
        }
    }

    /**
     * 查询，返回最少的属性信息
     *
     * @param xml
     * @return
     */
    public static Document queryByXMLMinWithCount(String xml, int count) throws Exception {
        StringBuffer operationXML = new StringBuffer();
        LinkedList parameters = new LinkedList();
        try {
            System.out.println("begin to query node:\n" + xml);
            if (xml != null && !"".equals(xml) && !"null".equalsIgnoreCase(xml)) {
                operationXML.append("<?xml version='1.0' encoding='utf-8' ?>\n<Query>\n");
                operationXML.append(xml + "\n");
                operationXML.append("</Query>\n");
                parameters.add(new C8Communication.Param(false, "Module", "Search"));
                parameters.add(new C8Communication.Param(false, "Operation", "QueryByXML"));
                parameters.add(new C8Communication.Param(false, "Fmt.Attr.Inc", "No"));
                parameters.add(new C8Communication.Param(false, "Fmt.Node.Info", "Min"));
                if (count > 0) {
                    parameters.add(new C8Communication.Param(false, "Qry.Limit.End", count + ""));
                }
                parameters.add(new C8Communication.Param(false, "Qry.XML", operationXML.toString()));

                return C8Communication.post(info, parameters, true);
            }

        } catch (Exception e) {
            if(checkSession()){
                log.error("NodeUtil执行失败，失败方法：queryByXMLMinWithCount,错误信息：{}",e.getMessage());
                log.info("尝试重新登陆");
                boolean b = reLogin(info);
                if(b){
                    log.info("重新登陆成功！");
                    return queryByXMLMinWithCount(xml,count);
                }
            }else{
                log.error("NodeUtil执行失败，失败方法：queryByXMLMinWithCount,错误信息：{}",e.getMessage(),e);
                throw e;
            }
        }
        return new Document();
    }

    /**
     * 查询某个对象的所有属性信息，使用HashMap存储
     *
     * @param URL
     * @return
     */
    public static HashMap<String, String> queryAttributes(String URL) throws Exception{
        HashMap<String, String> map = new HashMap();
        try {
            if (URL != null && !"".equals(URL) && !"null".equalsIgnoreCase(URL)) {
                Document xml = C8Communication.queryNode(info, URL);
                Element t = C8ResponseXML.elementWithCNL(xml, URL);
                if (t != null) {
                    map = elementToMap(t, URL);
                }
            }
        } catch (Exception e) {
            if(checkSession()){
                log.error("NodeUtil执行失败，失败方法：queryAttributes,错误信息：{}",e.getMessage());
                log.info("尝试重新登陆");
                boolean b = reLogin(info);
                if(b){
                    log.info("重新登陆成功！");
                    return queryAttributes(URL);
                }
            }else{
                log.error("NodeUtil执行失败，失败方法：queryAttributes,错误信息：{}",e.getMessage(),e);
                throw e;
            }
        }
        return map;
    }
    /**
     * @param enumListName
     * @param enumValueName
     * @return
     */
    public static String queryEnumDescValue(String enumListName, String enumValueName)throws Exception {
        StringBuffer operationXML = new StringBuffer();
        String desc="";
        operationXML.append("<Node Parameter=\"Type\" Op=\"EQ\" Value=\"EnumValue\" />\n");
        operationXML.append(
                "<Attribute Path=\"Child:__Parent__\" Id=\"Node Name\" Op=\"EQ\" SValue=\"" + enumListName + "\" />\n");
        operationXML.append("<Attribute Id=\"Node Name\" Op=\"EQ\" SValue=\"" + enumValueName + "\" />\n");
        Document retDoc = queryByXML(operationXML.toString());
        List<String> cnls = C8ResponseXML.resultNodeCNLs(retDoc);
        if (!(cnls == null || cnls.size() <= 0)) {
            Element styleElement = C8ResponseXML.elementWithCNL(retDoc, cnls.get(0));
            desc = (String)querySingleAttribute(styleElement,"Description",false);
        }
        return desc;
    }
    /**
     *
     * @param URL
     * @return
     * @author GHUANG
     * @version
     */
    public static Element queryElementbyUri(String URL) throws Exception{
        Element t = null;
        try {
            if (URL != null && !"".equals(URL) && !"null".equalsIgnoreCase(URL)) {
                Document xml = C8Communication.queryNode(info, URL);
                t = C8ResponseXML.elementWithCNL(xml, URL);
            }
        } catch (Exception e) {
            if(checkSession()){
                log.error("NodeUtil执行失败，失败方法：queryElementbyUri,错误信息：{}",e.getMessage());
                log.info("尝试重新登陆");
                boolean b = reLogin(info);
                if(b){
                    log.info("重新登陆成功！");
                    return queryElementbyUri(URL);
                }
            }else{
                log.error("NodeUtil执行失败，失败方法：queryElementbyUri,错误信息：{}",e.getMessage(),e);
                throw e;
            }

        }
        return t;
    }
    public static void initEnumResource() {
        if (enLocaleMap.isEmpty()) {
            enLocaleMap = getDisplayValueMap("en");
        }
        if (zhLocaleMap.isEmpty()) {
            zhLocaleMap = getDisplayValueMap("zh");
        }
    }
    /**
     * Element to Map 将查询的Document结果进行解析，处理单个Element信息，将属性字段保存在HashMap中
     *
     * @param resultElement
     * @param resultCNL
     * @return
     */
    private static HashMap<String, String> elementToMap(Element resultElement, String resultCNL) throws Exception{
        // TODO Auto-generated method stub
        HashMap<String, String> map = new HashMap<>();
        map.put("URL", resultCNL);
        String creator = resultElement.attributeValue("CR");
        String createTime = resultElement.attributeValue("CT");
        String modifier = resultElement.attributeValue("PB");
        String modifyTime = resultElement.attributeValue("PT");
        map.put("CR", queryExpressionResult("attr(\"Node Name\")", creator));
        map.put("CR.ID", queryExpressionResult("attr(\"UserID\")", creator));
        map.put("PB", queryExpressionResult("attr(\"Node Name\")", modifier));
        map.put("PB.ID", queryExpressionResult("attr(\"UserID\")", modifier));
        map.put("CT", toDate(createTime));
        map.put("CT.TIME", toTime(createTime));
        map.put("PT", toDate(modifyTime));
        map.put("PT.TIME", toTime(modifyTime));
        for (Element element : resultElement.elements()) {
            String ename = element.attributeValue("Id");
            String etype = element.attributeValue("Type");
            String evalue = element.attributeValue("Value");
            evalue = (evalue == null) ? "" : evalue;
            if (etype.contains("double") || etype.contains("integer") || etype.contains("float")) {
                if (!"".equals(evalue)) {
                    evalue = toCommonString(evalue);
                }
            }
            if (etype.contains("ref") || etype.contains("vector") || etype.contains("list")
                    || etype.contains("map")) {

                List<Element> values = element.elements();
                for (Element vEle : values) {
                    String value = vEle.getText();
                    if ("".equals(evalue)) {
                        evalue = value;
                    } else {
                        evalue = evalue + "," + value;
                    }
                }
            } else {
                if ("enum".equalsIgnoreCase(etype)) {
                    String display = enLocaleMap.get(evalue);
                    String displayZH = zhLocaleMap.get(evalue);
                    if (evalue.indexOf(":") > 0) {
                        if (!evalue.endsWith(":")) {
                            evalue = evalue.substring(evalue.indexOf(":") + 1);
                        } else {
                            evalue = "";
                        }
                    }
                    display = (display == null) ? evalue : display.trim();
                    displayZH = (displayZH == null) ? evalue : displayZH.trim();
                    map.put(ename + ".display", display);
                    map.put(ename + ".displayZH", displayZH);
                } else if ("time".equalsIgnoreCase(etype)) {
                    if (evalue == null || "0".equals(evalue)) {
                        evalue = "";
                    } else {
                        if (evalue.startsWith("0(")) {
                            evalue = "";
                        } else if (evalue.indexOf("(") > 0) {
                            String timeValue = evalue.substring(0, evalue.indexOf("("));
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                long timeLongValue = Long.parseLong(timeValue);
                                if (timeLongValue > 0) {
                                    Timestamp ts = new Timestamp(timeLongValue * 1000);
                                    evalue = sdf.format(ts);
                                }
                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                        }
                    }
                }
            }

            map.put(ename, evalue);
        }

        return map;
    }
    /**
     * 将数字格式的大数字，转换为字符串
     *
     * @param str
     * @return
     */
    public static String toCommonString(String str) {
        BigDecimal db = new BigDecimal(str);
        String ii = db.toPlainString();
        return ii;
    }
    /**
     * 将系统获得的时间值，转换为时间格式yyyy-MM-dd HH:mm:ss
     *
     * @param longStr
     * @return
     */
    public static String toTime(String longStr) {
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp ts = new Timestamp(Long.parseLong(longStr + "000"));
        return sm.format(ts);
    }
    /**
     * 将系统获得的时间值，转换为日期格式yyyy-MM-dd
     *
     * @param longStr
     * @return
     */
    public static String toDate(String longStr) {
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
        Timestamp ts = new Timestamp(Long.parseLong(longStr + "000"));
        return sm.format(ts);
    }

    /**
     * 将系统获得的时间值，转换为日期格式 并使用指定格式
     *
     * @param longStr
     * @param format
     *            yyyy-MM-dd yyyy/MM/dd
     * @return
     */
    public static String toDate(String longStr, String format) {
        SimpleDateFormat sm = new SimpleDateFormat(format);
        Timestamp ts = new Timestamp(Long.parseLong(longStr));
        return sm.format(ts);
    }

    /**
     * 执行表达式，并根据运行结果进行解析,多值默认采用逗号分隔
     *
     * @param expression
     * @param nodeCNL
     * @return
     */
    public static String queryExpressionResult(String expression, String nodeCNL) throws Exception{
        return queryExpressionResult(expression, nodeCNL, ",");
    }
    /**
     * 获取enumValue对应的翻译值，先获取中文翻译，如果没有则获取默认的英文翻译
     * @param enumValue
     * @return
     */
    public static String getEnumEnDisplay(String enumValue) {
        String displayCN = "";
        if (enumValue == null || "".equals(enumValue)) {
            enumValue = "";
        }
        if (enumValue.indexOf(":") >= 0 && !enumValue.endsWith(":")) {
            displayCN = NodeUtil.zhLocaleMap.get(enumValue);
            if (displayCN == null) {
                displayCN = NodeUtil.enLocaleMap.get(enumValue);
            }
            if (displayCN == null) {
                displayCN = "";
            }
        } else {
            displayCN = "";
        }

        return displayCN;
    }
    /**
     * 执行表达式，多值采用delim参数指定的分隔符分隔,如果没有指定，使用逗号分隔
     *
     * @param expression
     * @param nodeCNL
     * @param delim
     * @return
     */
    public static String queryExpressionResult(String expression, String nodeCNL, String delim) throws Exception {
        if (delim == null || "".equals(delim)) {
            delim = ",";
        }
        LinkedList parameters = new LinkedList();
        try {
            if (nodeCNL != null && !"".equals(nodeCNL) && !"null".equalsIgnoreCase(nodeCNL)) {
                parameters.add(new C8Communication.Param(false, "Module", "Expression"));
                parameters.add(new C8Communication.Param(false, "Operation", "TryJustExpression"));
                parameters.add(new C8Communication.Param(false, "URL", nodeCNL));
                parameters.add(new C8Communication.Param(false, "Expression", expression));
                Document doc = C8Communication.post(info, parameters, true);
                if (doc == null) {
                    return "";
                }
                List<Element> resultNodes = doc.getRootElement().elements("Result");
                if (resultNodes.size() > 0) {
                    Element firstElement = resultNodes.get(0);
                    String resultType = firstElement.attributeValue("Type");
                    String resultValue = firstElement.attributeValue("Value");
                    resultValue = (resultValue == null) ? "" : resultValue;
                    if (resultType.indexOf("ref") >= 0 || resultType.indexOf("vector") >= 0
                            || resultType.indexOf("list") >= 0 || resultType.indexOf("map") >= 0) {

                        List<Element> values = firstElement.elements();
                        for (int i = 0; i < values.size(); i++) {
                            Element vEle = values.get(i);
                            String value = vEle.getText();
                            if ("".equals(resultValue)) {
                                resultValue = value;
                            } else {
                                resultValue = resultValue + delim + value;
                            }
                        }
                    } else {
                        if ("enum".equalsIgnoreCase(resultType)) {
                            if (resultValue.indexOf(":") > 0) {
                                if (!resultValue.endsWith(":")) {
                                    resultValue = resultValue.substring(resultValue.indexOf(":") + 1);
                                } else {
                                    resultValue = "";
                                }
                            }
                        } else if ("time".equalsIgnoreCase(resultType)) {
                            if (resultValue == null || "0".equals(resultValue)) {
                                resultValue = "";
                            } else {
                                if (resultValue.startsWith("0(")) {
                                    resultValue = "";
                                } else if (resultValue.indexOf("(") > 0) {
                                    String timeValue = resultValue.substring(0, resultValue.indexOf("("));
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    try {
                                        long timeLongValue = Long.parseLong(timeValue);
                                        if (timeLongValue > 0) {
                                            Timestamp ts = new Timestamp(timeLongValue * 1000);
                                            resultValue = sdf.format(ts);
                                        }
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                    }
                                }
                            }
                        }
                    }
                    return resultValue;
                }
            }

        } catch (Exception e) {
            if(checkSession()){
                log.error("NodeUtil执行失败，失败方法：queryExpressionResult,错误信息：{}",e.getMessage());
                log.info("尝试重新登陆");
                boolean b = reLogin(info);
                if(b){
                    log.info("重新登陆成功！");
                    return queryExpressionResult(expression,nodeCNL,delim);
                }
            }else{
                log.error("NodeUtil执行失败，失败方法：queryExpressionResult,错误信息：{}",e.getMessage(),e);
                throw e;
            }
        }
        return "";
    }

    /**
     * 根据XML返回结果，如果是包含多个URL的，则返回多个URL的全部属性，存在JSONObject。
     *
     * @param xml
     * @return
     */
    public static JSONObject JSqueryByXML(String xml) throws Exception {
        StringBuffer operationXML = new StringBuffer();
        LinkedList parameters = new LinkedList();

        try {
            if (xml != null && !"".equals(xml) && !"null".equalsIgnoreCase(xml)) {
                operationXML.append("<?xml version='1.0' encoding='utf-8' ?>\n<Query>\n");
                operationXML.append(xml + "\n");
                operationXML.append("</Query>\n");
                parameters.add(new com.centricsoftware.commons.utils.C8Communication.Param(false, "Module", "Search"));
                parameters.add(new com.centricsoftware.commons.utils.C8Communication.Param(false, "Operation", "QueryByXML"));
                parameters.add(new com.centricsoftware.commons.utils.C8Communication.Param(false, "Qry.XML", operationXML.toString()));
                parameters.add(new com.centricsoftware.commons.utils.C8Communication.Param(false, "OutputJSON", "2"));
                JSONObject JSON =  com.centricsoftware.commons.utils.C8Communication.JSpost(info, parameters, true);

                return JSON;
            }

        } catch (Exception e) {
            if(checkSession()){
                log.error("NodeUtil执行失败，失败方法：JSqueryByXML,错误信息：{}",e.getMessage());
                log.info("尝试重新登陆");
                boolean b = reLogin(info);
                if(b){
                    log.info("重新登陆成功！");
                    return JSqueryByXML(xml);
                }
            }else{
                log.error("NodeUtil执行失败，失败方法：JSqueryByXML,错误信息：{}",e.getMessage(),e);
                throw e;
            }
        }
        return new JSONObject();
    }

    /**
     * 最小化查询，查询结果中只返回Node Name和URL，查询效率高
     *
     * @param xml
     * @return
     */
    public static JSONObject JSqueryByXMLMin(String xml) throws Exception {
        StringBuffer operationXML = new StringBuffer();
        LinkedList parameters = new LinkedList();
        try {
            if (StrUtil.isNotBlank(xml)) {
                operationXML.append("<?xml version='1.0' encoding='utf-8' ?>\n<Query>\n");
                operationXML.append(xml + "\n");
                operationXML.append("</Query>\n");
                parameters.add(new com.centricsoftware.commons.utils.C8Communication.Param(false, "Module", "Search"));
                parameters.add(new com.centricsoftware.commons.utils.C8Communication.Param(false, "Operation", "QueryByXML"));
                parameters.add(new com.centricsoftware.commons.utils.C8Communication.Param(false, "Fmt.Attr.Inc", "No"));
                parameters.add(new com.centricsoftware.commons.utils.C8Communication.Param(false, "Fmt.Node.Info", "Min"));
                parameters.add(new com.centricsoftware.commons.utils.C8Communication.Param(false, "OutputJSON", "2"));
                parameters.add(new com.centricsoftware.commons.utils.C8Communication.Param(false, "Qry.XML", operationXML.toString()));

                return com.centricsoftware.commons.utils.C8Communication.JSpost(info, parameters, true);
            }

        } catch (Exception e) {
            if(checkSession()){
                log.error("NodeUtil执行失败，失败方法：JSqueryByXMLMin,错误信息：{}",e.getMessage());
                log.info("尝试重新登陆");
                boolean b = reLogin(info);
                if(b){
                    log.info("重新登陆成功！");
                    return JSqueryByXMLMin(xml);
                }
            }else{
                log.error("NodeUtil执行失败，失败方法：JSqueryByXMLMin,错误信息：{}",e.getMessage(),e);
                throw e;
            }
        }
        return new JSONObject();
    }

    /**
     * 执行表达式，并根据运行结果进行解析,多值默认采用逗号分隔
     *
     * @param expression
     * @param nodeCNL
     * @return
     */
    public static String JSqueryExpressionResult(String expression, String nodeCNL) throws Exception {
        return JSqueryExpressionResult(expression, nodeCNL, ",");
    }

    /**
     * 执行表达式，多值采用delim参数指定的分隔符分隔,如果没有指定，使用逗号分隔
     *
     * @param expression
     * @param nodeCNL
     * @param delim
     * @return
     */
    public static String JSqueryExpressionResult(String expression, String nodeCNL, String delim) throws Exception {
        if (delim == null || "".equals(delim)) {
            delim = ",";
        }
        String resultValue = "";
        String value;
        LinkedList parameters = new LinkedList();
        try {
            if (nodeCNL != null && !"".equals(nodeCNL) && !"null".equalsIgnoreCase(nodeCNL)) {
                parameters.add(new com.centricsoftware.commons.utils.C8Communication.Param(false, "Module", "Expression"));
                parameters.add(new com.centricsoftware.commons.utils.C8Communication.Param(false, "Operation", "TryJustExpression"));
                parameters.add(new com.centricsoftware.commons.utils.C8Communication.Param(false, "URL", nodeCNL));
                parameters.add(new com.centricsoftware.commons.utils.C8Communication.Param(false, "Expression", expression));
                parameters.add(new com.centricsoftware.commons.utils.C8Communication.Param(false, "OutputJSON", "2"));
                JSONObject doc = com.centricsoftware.commons.utils.C8Communication.JSpost(info, parameters, true);
                if (doc == null) {
                    return "";
                }
                JSONObject resultNodes = doc.getJSONObject("Result");
                JSONArray  TypeList = new JSONArray();
                if (!resultNodes.isEmpty()) {
                    String resultType = resultNodes.getStr("Type");
                    if (resultType.indexOf("vector") >= 0 || resultType.indexOf("list") >= 0 || resultType.indexOf("set") >= 0 ) {
                        try {
                            if(resultType.indexOf("enum") >= 0) {
                                TypeList = resultNodes.getJSONArray("enum");
                                for (int i = 0; i < TypeList.size(); i++) {
                                    value = TypeList.getStr(i);
                                    if ("".equals(resultValue)) {
                                        resultValue = value;
                                    } else {
                                        resultValue = resultValue + delim + value;
                                    }
                                }
                            }else {
                                TypeList = resultNodes.getJSONArray("ref");
                                for (int i = 0; i < TypeList.size(); i++) {
                                    value = TypeList.getStr(i);
                                    if ("".equals(resultValue)) {
                                        resultValue = value;
                                    } else {
                                        resultValue = resultValue + delim + value;
                                    }
                                }
                            }

                        }catch (Exception e) {
                            resultValue = resultNodes.getStr("ref");
                        }
                    }else if(resultType.indexOf("map") >= 0) {
                        TypeList = resultNodes.getJSONArray("ref");
                        for (int i = 0; i < TypeList.size(); i++) {
                            JSONObject JSRes = TypeList.getJSONObject(i);
                            value = JSRes.getStr("$");
                            if ("".equals(resultValue)) {
                                resultValue = value;
                            } else {
                                resultValue = resultValue + delim + value;
                            }
                        }
                    }else {
                        resultValue = resultNodes.getStr("Value");
                        if ("enum".equalsIgnoreCase(resultType)) {
                            if (resultValue.indexOf(":") > 0) {
                                if (!resultValue.endsWith(":")) {
                                    try {
                                        String zhRes = enLocaleMap.get(resultValue);
                                        String enRes = zhLocaleMap.get(resultValue);
                                        resultValue = resultValue.substring(resultValue.indexOf(":") + 1)+","+zhRes+","+enRes;
                                    }catch (Exception e) {
                                        resultValue = resultValue.substring(resultValue.indexOf(":") + 1);
                                    }
                                } else {
                                    resultValue = "";
                                }
                            }
                        } else if ("time".equalsIgnoreCase(resultType)) {
                            if (resultValue == null || "0".equals(resultValue)) {
                                resultValue = "";
                            } else {
                                if (resultValue.startsWith("0(")) {
                                    resultValue = "";
                                } else if (resultValue.indexOf("(") > 0) {
                                    String timeValue = resultValue.substring(0, resultValue.indexOf("("));
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    try {
                                        long timeLongValue = Long.parseLong(timeValue);
                                        if (timeLongValue > 0) {
                                            resultValue = sdf.format(timeLongValue);
                                        }
                                    } catch (Exception e) {
                                        throw e;
                                    }
                                }
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
            if(checkSession()){
                log.error("NodeUtil执行失败，失败方法：JSqueryExpressionResult,错误信息：{}",e.getMessage());
                log.info("尝试重新登陆");
                boolean b = reLogin(info);
                if(b){
                    log.info("重新登陆成功！");
                    return JSqueryExpressionResult(expression,nodeCNL,delim);
                }
            }else{
                log.error("NodeUtil执行失败，失败方法：JSqueryExpressionResult,错误信息：{}",e.getMessage(),e);
                throw e;
            }
        }
        return resultValue;
    }

    /**
     *  查询单个URL的属性，返回JSONObject对象，通过JSONObject.getString(KEY)得到String的Value值。
     *
     * @param URL
     * @return
     */
    public static HashMap<String, String> JSqueryAttributes(String URL) throws Exception {
        JSONObject Attrs = JSONUtil.createObj();
        JSONArray AttrsList;
        JSONObject Json;
        try {
            if (StrUtil.isNotBlank(URL)) {
                Json = com.centricsoftware.commons.utils.C8Communication.JSqueryNode(info, URL);
                if (!JSONUtil.isNull(Json)) {
                    JSONObject Nodeobj = Json.getJSONObject("NODES");
                    AttrsList = Nodeobj.getJSONArray("ResultNode");
                    if (AttrsList.size() > 0){
                        // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                        Attrs  = AttrsList.getJSONObject(0);
                    }
                }
            };
        } catch (Exception e) {
            if(checkSession()){
                log.error("NodeUtil执行失败，失败方法：JSqueryAttributes,错误信息：{}",e.getMessage());
                log.info("尝试重新登陆");
                boolean b = reLogin(info);
                if(b){
                    log.info("重新登陆成功！");
                    return JSqueryAttributes(URL);
                }
            }else{
                log.error("NodeUtil执行失败，失败方法：JSqueryAttributes,错误信息：{}",e.getMessage(),e);
                throw e;
            }
        }
        return JsonToMap(Attrs,URL);
    }

    /**
     * JSON to Map 将查询的JSON结果进行解析，如果为多值查询，则返回第一个值的所有属性的信息，将属性字段保存在HashMap中
     *
     * @param ResultJson
     * @param resultCNL
     * @return
     */
    public static HashMap<String,String> JsonToMap(JSONObject ResultJson, String resultCNL) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("URL", resultCNL);
        String creator = ResultJson.getStr("$CR");
        String createTime = ResultJson.getStr("$CT");
        String modifier = ResultJson.getStr("$PB");
        String modifyTime = ResultJson.getStr("$PT");
        map.put("CR", JSqueryExpressionResult("attr(\"Node Name\")", creator));
        map.put("CR.ID", JSqueryExpressionResult("attr(\"UserID\")", creator));
        map.put("PB", JSqueryExpressionResult("attr(\"Node Name\")", modifier));
        map.put("PB.ID", JSqueryExpressionResult("attr(\"UserID\")", modifier));
        map.put("CT", toDate(createTime));
        map.put("CT.TIME", toTime(createTime));
        map.put("PT", toDate(modifyTime));
        map.put("PT.TIME", toTime(modifyTime));
        JSONObject ResultTypes = ResultJson.getJSONObject("$attrs");
        //将属性和属性值数放入HashMap
        for (Map.Entry<String, Object> entry : ResultJson.entrySet()) {
            String str = entry.getKey();
            if("$attrs".equalsIgnoreCase(str)) continue;
            String ename = str;
            String etype ="";
            if(ResultTypes.containsKey(ename)) {
                if(ResultTypes.getJSONObject(ename).containsKey("Type")) {
                    etype = ResultTypes.getJSONObject(ename).getStr("Type");
                }
            }else {
                etype = "String";
            };
            String evalue = ResultJson.getStr(str);
            evalue = (evalue == null) ? "" : evalue;
            if (etype.indexOf("double") >= 0 || etype.indexOf("integer") >= 0 || etype.indexOf("float") >= 0
                    || etype.indexOf("integer") >= 0) {
//					专门区分一下double vector 一般用在尺码上，如果不做单独处理会报错
                if("doublevector".equalsIgnoreCase(etype) && !"".equals(evalue)) {
//	            	让它去执行vector的处理方式
                }else{
                    if(!"".equals(evalue)) {
                        evalue = toCommonString(evalue);
                    }
                }
            }
            if (etype.indexOf("set") >= 0 ||etype.indexOf("vector") >= 0 || etype.indexOf("list") >= 0) {
                try {
                    String value2 = "";
                    JSONArray values = JSONUtil.parseArray(evalue);
                    for (int i = 0; i < values.size(); i++) {
                        String value = values.getStr(i);
                        if("".equalsIgnoreCase(value2)) {
                            evalue = value;
                            value2 = value;
                        }else {
                            evalue = evalue + "," + value;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(ename);
                }
            } else if( etype.indexOf("map") >= 0 ){
                String value2 = "";
                for (Map.Entry<String, Object> map1 : ResultJson.getJSONObject(str).entrySet()) {
                    if("".equalsIgnoreCase(value2)) {
                        evalue = (String) map1.getValue();
                        value2 = evalue;
                    }else {
                        evalue = evalue + "," + (String) map1.getValue();
                    };

                }
            }else {
                if ("enum".equalsIgnoreCase(etype)) {
                    String display = enLocaleMap.get(evalue);
                    String displayZH = zhLocaleMap.get(evalue);
                    if (evalue.indexOf(":") > 0) {
                        if (!evalue.endsWith(":")) {
                            evalue = evalue.substring(evalue.indexOf(":") + 1);
                        } else {
                            evalue = "";
                        }
                    }
                    display = (display == null) ? evalue : display.trim();
                    displayZH = (displayZH == null) ? evalue : displayZH.trim();
                    map.put(ename + ".display", display);
                    map.put(ename + ".displayZH", displayZH);
                } else if ("time".equalsIgnoreCase(etype)) {
                    if (evalue == null || "0".equals(evalue)) {
                        evalue = "";
                    } else {
                        if (evalue.startsWith("0(")) {
                            evalue = "";
                        } else if (evalue.indexOf("(") > 0) {
                            String timeValue = evalue.substring(0, evalue.indexOf("("));
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                long timeLongValue = Long.parseLong(timeValue);
                                if (timeLongValue > 0) {
                                    Timestamp ts = new Timestamp(timeLongValue * 1000);
                                    evalue = sdf.format(ts);
                                }
                            } catch (Exception e) {
                                throw e;
                            }
                        }
                    }
                }
            }
            map.put(ename, evalue);
        }
        return map;
    }

    /**
     * 最小化查询对象，只返回结果对象的URL，并将查询语句和结果存放到log中
     *
     * @param xml
     * @return
     */
    public static List<String> queryBOByXML(String xml) throws Exception{
        StringBuffer operationXML = new StringBuffer();
        LinkedList parameters = new LinkedList();
        List list = new ArrayList();
        try {
            if (xml != null && !"".equals(xml) && !"null".equalsIgnoreCase(xml)) {
                operationXML.append("<?xml version='1.0' encoding='utf-8' ?>\n<Query>\n");
                operationXML.append(xml + "\n");
                operationXML.append("</Query>\n");
                parameters.add(new C8Communication.Param(false, "Module", "Search"));
                parameters.add(new C8Communication.Param(false, "Operation", "QueryByXML"));
                parameters.add(new C8Communication.Param(false, "Fmt.Attr.Inc", "No"));
                parameters.add(new C8Communication.Param(false, "Fmt.Node.Info", "Min"));
                parameters.add(new C8Communication.Param(false, "Qry.XML", operationXML.toString()));
                log.info("xml={}",xml);
                Document doc = new C8Communication().post(info, parameters, true);
                list = C8ResponseXML.resultNodeCNLs(doc);
                log.info("query list:{}" , list);
            }

        } catch (Exception e) {
            if(checkSession()){
                log.error("NodeUtil执行失败，失败方法：queryBOByXML,错误信息：{}",e.getMessage());
                log.info("尝试重新登陆");
                boolean b = reLogin(info);
                if(b){
                    log.info("重新登陆成功！");
                    return queryBOByXML(xml);
                }
            }else{
                log.error("NodeUtil执行失败，失败方法：queryByXMLMinWithCount,错误信息：{}",e.getMessage(),e);
                throw e;
            }
        }
        return list;
    }

    public static Object querySingleAttribute(Element elm, String attr, boolean islist) throws Exception{
        if(elm==null){
            return "";
        }
        Object value;
        if (islist) {
            XPath cwcpath = new XPath("Attribute[@Id='" + attr + "']");
            value = C8ResponseXML.extractAttributeListValue(elm, cwcpath);
        } else {
            value = C8ResponseXML.extractAttributeValue(elm, attr);
            if (value == null) {
                value = "";
            }
        }
        return value;
    }

    /**
     * element方式查询单属性
     * @param elm
     * @param attr
     * @return
     * @throws Exception
     */
    public static List<String> querySingleAttributeList(Element elm, String attr) throws Exception{
        XPath cwcpath = new XPath("Attribute[@Id='" + attr + "']");
        return C8ResponseXML.extractAttributeListValue(elm, cwcpath);
    }

    /**
     * element方式查询单属性
     * @param elm
     * @param attr
     * @return
     * @throws Exception
     */
    public static String querySingleAttribute(Element elm, String attr) throws Exception{
        return querySingleAttribute(elm,attr,false).toString();
    }

    /**
     * 查询node单属性
     * @param url
     * @param attr
     * @param isList
     * @return list或者string
     * @throws Exception
     */
    public static Object querySingleAttrByUrl(String url,String attr,boolean isList) throws Exception{
        Element element = queryElementbyUri(url);
        return querySingleAttribute(element,attr,isList);
    }
    /**
     * 查询node 列表属性
     * @param url
     * @param attr
     * @return list或者string
     * @throws Exception
     */
    public static List<String> querySingleAttrByUrlList(String url,String attr) throws Exception{
        Element element = queryElementbyUri(url);
        return querySingleAttributeList(element,attr);
    }
    /**
     * 查询node单属性
     * @param url
     * @param attr
     * @return 单属性Object,默认该属性非list
     * @throws Exception
     */
    public static Object querySingleAttrByUrl(String url,String attr) throws Exception{
        if(url==null || "centric:".equals(url)){
            return "";
        }
        Element element = queryElementbyUri(url);
        return querySingleAttribute(element,attr,false);
    }


    /**
     * 测试NodeUtil中的主要方法
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String queryXml = "<Node Parameter=\"Type\" Op=\"EQ\" Value=\"Style\" />";

        JSONObject jsonObject = NodeUtil.JSqueryByXML(queryXml);
        System.out.println(JSONUtil.toJsonPrettyStr(jsonObject));

        JSONObject jsonObject1 = NodeUtil.JSqueryByXMLMin(queryXml);
        System.out.println(JSONUtil.toJsonPrettyStr(jsonObject1));

        String nodeUrl = "C1257";//node url
        HashMap<String, String> allNodeAttrs = NodeUtil.JSqueryAttributes(nodeUrl);
        System.out.println(StrUtil.toString(allNodeAttrs));

        String expression = "block(attr(\"Node Name\"))";//表达式
        String result1 = NodeUtil.JSqueryExpressionResult(expression, nodeUrl);
        System.out.println(result1);

        String delim = "^^";//返回多值的分隔符，默认是逗号隔开
        String result2 = NodeUtil.JSqueryExpressionResult(nodeUrl, expression, delim);
        System.out.println(result2);
    }


    public static Object reExecute(Exception e,String methodName, Object... params) throws Exception{
        Object returnValue = null;
        if(checkSession()){
            log.error("NodeUtil执行失败，失败方法：processNode,错误信息：{}",e.getMessage());
            log.info("尝试重新登陆");
            boolean b = reLogin(info);
            if(b){
                log.info("重新登陆成功！");
                Class clazz = NodeUtil.class;
                if (params != null && params.length > 0) {
                    Class[] paramTypes = new Class[params.length];
                    for (int i = 0; i < params.length; i++) {
                        paramTypes[i] = params[i].getClass();
                    }
                    Method method = clazz.getMethod(methodName, paramTypes);
                    returnValue = method.invoke(null, params);
                } else {
                    Method method = clazz.getMethod(methodName);
                    returnValue = method.invoke(null);
                }
                return returnValue;
            }
        }else{
            log.error("NodeUtil执行失败，失败方法：processNode,错误信息：{}",e.getMessage(),e);
            throw e;
        }
        return returnValue;
    }


    /**
     *
     * 将searchXml转换成NativeExport的xml
     *
     * @param searchXml
     * @return
     */
    public static String getNativeExportXml(String searchXml) throws Exception {
        try {
            Map map = Maps.newHashMap();
            map.put("Module", "NodeBrowserSupport");
            map.put("Operation", "TranslateQueryToDBXML");
            map.put("Qry.Limit.Path", "");
            map.put("OutputJSON", "2");
            map.put("Qry.XML", "<Query>" + searchXml + "</Query>");
            map.put("Qry.Limit.End", "10");
            String result = HttpRequest.post(api)
                    .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8")
                    .form(map)
                    .execute().body();
            JSONObject obj = JSONUtil.parseObj(result);
            if ("Successful".equals(obj.getStr("Status"))) {
                JSONObject query = obj.getJSONObject("DBQuery");
                String xml = query.getStr("XML");
                return xml;
            }
        } catch (HttpException e) {
            reExecute(e,"getNativeExportXml",searchXml);
        }
        return "";
    }

    /**
     * 统一校验是否需要重新登录
     *
     * @param obj
     * @return
     */
    public static boolean checkHttpLogin(JSONObject obj) {
        String error = obj.getStr("Error");
        String errorAdmin = obj.getStr("ErrorAdmin");
        if ("SRV_ERR_SiteAdmin_INVALID_SESSION".equals(error) || "The client session has expired or is invalid.".equals(errorAdmin)) {
            return true;
        }
        return false;
    }

    /**
     * 根据URL查找数据
     *
     * @param url
     * @return
     */
    public static HashMap<String, JSONObject> queryByURL(String url) {
        if (url == null) url = "";
        return queryByURLs(url.split(","));
    }

    /**
     * 查询多个URL数据，一起返回多条数据
     *
     * @param urls
     * @return
     */
    public static HashMap<String, JSONObject> queryByURLs(String[] urls) {
        return queryByURLs(urls, null);
    }

    public static DepPathResult queryDepPathByXML(DepPath depPath) {
        return queryDepPathByXML(depPath, 1, 4999);
    }

    public static DepPathResult queryDepPathByXML(DepPath depPath, int begin, int end) {
        Map map = Maps.newHashMap();
        map.put("Module", "Search");
        map.put("Operation", "QueryByXML");
        map.put("OutputJSON", "2");
        map.put("Qry.Limit.Filter", "10000");
        map.put("Fmt.Complete", "Ref");
        map.put("Qry.Limit.Begin", begin);
        map.put("Qry.Limit.End", end);
        map.put("Fmt.Complete.Max", 4999);
        map.put("Qry.XML", depPath.getXml());
        StringBuilder params = new StringBuilder("");
        int i = 0;
        for (String s : depPath.getPaths()) {
            if (i == 0) {
                params.append("?Dep.Path=" + s);
            } else {
                params.append("&Dep.Path=" + s);
            }
            i++;
        }
        JSONObject result = NodeUtil.postHttp(map, api + params.toString());
        if(StrUtil.isEmpty(result.getStr("NODES"))){
            return DepPathResult.init(null, null, depPath,null);
        }else{
            JSONObject nodes = result.getJSONObject("NODES");
            JSONObject completeResultRefs = result.getJSONObject("CompleteResultRefs");
            JSONArray node = nodes.getJSONArray("Node");
            JSONArray resultNode = nodes.getJSONArray("ResultNode");
            return DepPathResult.init(resultNode, node, depPath,completeResultRefs);
        }

    }

    public static  DepPathResult  queryDepPathByUrl(DepPath depPath) {
        Map map = Maps.newHashMap();
        map.put("Module", "Search");
        map.put("Operation", "QueryByURL");
        map.put("OutputJSON", "2");
        StringBuilder params = new StringBuilder("");
        int i = 0;
        for (String s : depPath.getUrls()) {
            if (i == 0) {
                params.append("?Qry.URL=" + s);
            } else {
                params.append("&Qry.URL=" + s);
            }
            i++;
        }
        for (String s : depPath.getPaths()) {
            if (StrUtil.isBlank(params.toString())) {
                params.append("?Dep.Path=" + s);
            } else {
                params.append("&Dep.Path=" + s);
            }
        }
        JSONObject result = NodeUtil.postHttp(map, api + params.toString());
        JSONObject nodes = result.getJSONObject("NODES");
        JSONArray node = nodes.getJSONArray("Node");
        JSONArray resultNode = nodes.getJSONArray("ResultNode");
        return DepPathResult.init(resultNode, node, depPath);
    }
    /**
     * 查询多个URL数据，一起返回多条数据
     *
     * @param urls
     * @param depPaths 关系路径
     * @return
     */

    public static HashMap<String, JSONObject> queryByURLs(String[] urls, String[] depPaths) {
        DepPath build = DepPath.builder().addUrls(urls).addPaths(depPaths).build();
        DepPathResult depPathResult = NodeUtil.queryDepPathByUrl(build);
        HashMap<String, JSONObject> allNodes = depPathResult.getAllNodes();
        return allNodes;
    }
    public static JSONObject postHttp(Map map, String httpUrl) {
        String result = HttpRequest.post(httpUrl)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8")
                .form(map).execute().body();
        JSONObject obj = JSONUtil.parseObj(result);
        if ("Successful".equals(obj.getStr("Status"))) {
            return obj;
        }
        if (checkHttpLogin(obj)) {
            log.info("尝试重新登陆");
            boolean b = reLogin(info);
            if(b){
                log.info("重新登陆成功！");
                return postHttp(map,httpUrl);
            }
        }
        return null;

    }


}
