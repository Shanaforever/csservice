package com.centricsoftware.commons.utils;
import cn.hutool.core.util.StrUtil;
import com.centricsoftware.config.entity.CsProperties;
import com.centricsoftware.pi.export.importer.ImportInformation.ImportItem;
import com.centricsoftware.pi.export.importer.processor.NodeMapper;
import com.centricsoftware.pi.export.importer2.processor.ImportMapper;
import com.centricsoftware.pi.export.importer2.processor.ImportNode;
import com.centricsoftware.pi.export.query.DbConnection;
import com.centricsoftware.pi.export.query.VerticalExtract;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.*;

@Slf4j
public class NodeUrl {
    public static String DBQUERY = "DBQuery";
    private DbConnection q = null;
    public String urlId = null;
    public static String centricRef = null;
    public static int querylimit = 900;

    public static CsProperties properties=NodeUtil.getProperties();
    public NodeUrl(DbConnection query) {
        q = query;
        // Query for centric:
        if (centricRef == null) {
            centricRef = q.getId("centric:");
        }
    }

    public NodeUrl() {

    }

    public boolean setNodeUrl(ImportNode node, NodeMapper nm, String url, String path) {
        if (path != null) {
            int p = path.indexOf(':');
            if (p >= 0) {
                path = path.substring(p + 1);
            }
            String url2 = queryForUrlPath(url, path);
            if (url2 != null) {
                url = url2;
            } else
                // Didn't find url for the path
                // Assuming path is for attributes needed for server op
                if (node.url == null) {
                    node.url = url;
                }
        }
        if (node.nodeType.equals(nm.nodeType)) {
            node.url = url;
        }
        nm.url = url;
        node.worker.bUniqueRow = true;
        node.worker.bCreated = false;
        node.bCreate = false;
        return true;
    }

    public String getUrl(String id, String type) {
        // Escape id
        String newId = stripBadChar(id);
        newId = newId.replace('&', ' ');
        newId = newId.replace(';', ' ');
        newId = newId.replace('\'', ' ');
        newId = newId.replace('#', ' ');

        String u;
        u = "C0/" + newId + "|" + type;

        return u;
    }

    public String getUrlBase(String id, String type) {
        // Escape id
        String newId = stripBadChar(id);
        newId = newId.replace('&', ' ');
        newId = newId.replace(';', ' ');
        newId = newId.replace('\'', ' ');
        newId = newId.replace('#', ' ');

        // Check for revisable node
        int p = type.indexOf("Revision");
        if (p > 0) {
            type = type.substring(0, p);
        }

        String u = "C0/" + newId + "|" + type;
        if (p > 0) {
            u += "?Path=Child:CurrentRevision";
        }

        return u;
    }


    public String stripBadChar(String s) {
        StringBuilder out = new StringBuilder(s.length() * 6);
        byte[] bytes = s.getBytes();

        for (int j = 0; j < bytes.length; ++j) {
            byte b = bytes[j];
            int i = b;
            if (i < 0) {
                i = 256 + i;
            }
            if (i > '~') {
                String cc = "&#" + i + ";";
                out.append(cc);
            } else {
                char current = (char) b;
                if ((current == 0x9) ||
                        (current == 0xA) ||
                        (current == 0xD) ||
                        ((current >= 0x20) && (current <= 0xD7FF)) ||
                        ((current >= 0xE000) && (current <= 0xFFFD)) ||
                        ((current >= 0x10000) && (current <= 0x10FFFF))) {
                    out.append(current);
                }
            }
        }

        s = out.toString();
        return s;

    }

    /**
     *
     * @param key
     * @param objtype
     * @param urlist
     * @return
     * @author GHUANG
     * @version 2019年10月23日 下午3:58:57
     */
    public LinkedHashMap<String, ArrayList<String>> queryList(String key, String objtype,
                                                              ArrayList<HashMap<String, String>> urlist) {
        LinkedHashMap<String, ArrayList<String>> sumMap = new LinkedHashMap();
        int qamount = Integer.parseInt(properties.getValue(key + "query.amount", "0"));
        String querystr = properties.getValue(key + "query.spec", "");
        for (int i = 1; i < qamount; i++) {
            String name = properties.getValue(key + "query." + i + ".attrname", "");
            String opera = properties.getValue(key + "query." + i + ".opera", "EQ");
            String path = properties.getValue(key + "query." + i + ".attrpath", "");
            String value = properties.getValue(key + "query." + i + ".value", "");
            querystr += "<Predicate Path=\"" + path + "\" AttributeName=\"" + name + "\" Operand=\"" + opera
                    + "\" ValueString=\"" + value + "\" />";
        }
        String order = properties.getValue(key + "order", "");
        ArrayList<String> queryXMList = getQueryXML(objtype, urlist, querystr, order);
        log.info("query size={}",queryXMList.size());
        VerticalExtract verticalExtract = new VerticalExtract(q);
        for (int x = 0; x < queryXMList.size(); x++) {
            String queryXML = queryXMList.get(x);
            ArrayList<HashMap<String, String>> exlist = new ArrayList();
            String special = properties.getValue(key + "detail.spec", "");
            int damount = Integer.parseInt(properties.getValue(key + "detail.amount", "0"));
            for (int i = 1; i < damount; i++) {
                String name = properties.getValue(key + "detail." + i + ".attrname", "");
                String path = properties.getValue(key + "detail." + i + ".attrpath", "");
                special += "<DETAIL AttributeName=\"" + name + "\" Path=\"" + path + "\" Seq=\"" + i + "\"/>";
            }
            String extractXML = getExtractXML(exlist, special);
            log.info("queryXML={}" , queryXML);
            log.info("extractXML={}" , extractXML);

            // Query query = new Query(conn.q);
            ResultSet rs = verticalExtract.getData(queryXML, extractXML, "");// conn.q.getData(queryXML, // extractXML);
            LinkedHashMap<String, ArrayList<String>> rMap = getDataResultMap(rs, key);
            sumMap.putAll(rMap);
            // rMap.clear();
            rMap = null;

        }
        verticalExtract.close();
        queryXMList = null;
        return sumMap;
    }

    /**
     *
     * @param key
     * @param objtype
     * @param urlist
     * @return
     * @author GHUANG
     * @version 2019年10月23日 下午3:58:57
     */
    public LinkedHashMap<String, HashMap<String, String>> queryAttrList(String key, String objtype,
                                                                        ArrayList<HashMap<String, String>> urlist, HashMap<String, String> querymap) {

        int qamount = Integer.parseInt(properties.getValue(key + "query.amount", "0"));
        String querystr = properties.getValue(key + "query.spec", "");
        for (int i = 1; i < qamount; i++) {
            String name = properties.getValue(key + "query." + i + ".attrname", "");
            String opera = properties.getValue(key + "query." + i + ".opera", "EQ");
            String path = properties.getValue(key + "query." + i + ".attrpath", "");
            String value = "";
            if (querymap != null) {
                value = querymap.get(String.valueOf(i));
            }
            if (value == null) {
                value = properties.getValue(key + "query." + i + ".value", "");
            }
            querystr += "<Predicate Path=\"" + path + "\" AttributeName=\"" + name + "\" Operand=\"" + opera
                    + "\" ValueString=\"" + value + "\" />";
        }
        String order = properties.getValue(key + "order", "");
        LinkedHashMap<String, HashMap<String, String>> sumMap = new LinkedHashMap();
        ArrayList<String> queryXMList = getQueryXML(objtype, urlist, querystr, order);
        log.info(queryXMList.size() + " queryXML={}" , queryXMList);
        for (int x = 0; x < queryXMList.size(); x++) {
            String queryXML = queryXMList.get(x);
            ArrayList<HashMap<String, String>> exlist = new ArrayList();
            String special = properties.getValue(key + "detail.spec", "");
            int damount = Integer.parseInt(properties.getValue(key + "detail.amount", "0"));
            for (int i = 1; i < damount; i++) {
                String name = properties.getValue(key + "detail." + i + ".attrname", "");
                String path = properties.getValue(key + "detail." + i + ".attrpath", "");
                if (path.length() > 0) {
                    path = "Path=\"" + path + "\"";
                }
                special += "<DETAIL AttributeName=\"" + name + "\" " + path + " Seq=\"" + i + "\"/>";
            }
            String extractXML = getExtractXML(exlist, special);
            log.info("extractXML={}" , extractXML);
            VerticalExtract verticalExtract = new VerticalExtract(q);
            // Query query = new Query(conn.q);
            ResultSet rs = verticalExtract.getData(queryXML, extractXML, "");// conn.q.getData(queryXML, // extractXML);
            LinkedHashMap<String, HashMap<String, String>> rMap = getDataResultMapAttr(rs, key,
                    DBQUERY);
            sumMap.putAll(rMap);
            rMap.clear();
            rMap = null;
            verticalExtract.close();
        }
        queryXMList = null;

        return sumMap;
    }

    private String queryForUrlPath(String nodeUrl, String path) {
        String url = null;

        // Need to check if we have cached the result

        String query = "<QUERY><AND><Predicate NodeURL=\"" + CommonUtil.escapeUrl(nodeUrl) + "\" Operand=\"EQ\"/>";
        query += "</AND></QUERY>";

        String detail = "<EXTRACT><DETAIL AttributeName=\"" + path + "\" Seq=\"1\"/></EXTRACT>";
        ResultSet rs = q.getData(query, detail);
        if (rs == null) {
            q.closeQuery();
            return null;
        }

        try {
            if (rs.next()) {
                url = rs.getString(6);
            }
        } catch (Exception e) {
        }
        q.closeQuery(rs);
        if (url != null) {
            url = q.getURL(url);
        }

        return url;
    }

    /**
     *  传入属性值找到对应的nodeurl
     * @param nodeType nodeType
     * @param attribute attribute
     * @param value value
     * @return 对应node的url
     */
    public String queryForUrl(String nodeType, String attribute, String value) {
        String url = null;
        String query = "<QUERY><AND><Predicate NodeType=\"" + nodeType + "\" Operand=\"EQ\"/>";
        query += "<Predicate AttributeName=\"" + attribute + "\" Operand=\"EQ\" ValueString=\"" + value + "\"/>";
        query += "</AND></QUERY>";
        String detail = "<EXTRACT><DETAIL AttributeName=\"" + attribute + "\" Seq=\"1\"/></EXTRACT>";
        ResultSet rs = q.getData(query, detail);
        if (rs == null) {
            q.closeQuery();
            return null;
        }
        try {
            if (rs.next()) {
                url = rs.getString(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        q.closeQuery(rs);
        if (url != null) {
            url = q.getURL(url);
        }
        return url;
    }

    public String addToQuery(String query, ImportMapper mapper, String column) {
        String qry = null;

        qry = addToQuery(query, mapper.nodeType, mapper.attr.item.query, column, mapper.attr.item.path,
                mapper.attr.item);
        return qry;
    }

    private String addToQuery(String query, String nodeType, String name, String value, String path, ImportItem item) {
        String qry = null;
        if (query == null) {
            qry = "<QUERY><AND><Predicate NodeType=\"" + nodeType + "\" Operand=\"EQ\"/>";
        } else {
            qry = query;
        }

        if (path != null && (value == null || value.length() == 0)) {
            String attrName = name;

            List<String> tokens = tokenize(path, "/");
            qry += "<OR>";
            String prevPath = "";
            for (String token : tokens) {
                int p = token.indexOf("Child:");
                if (p >= 0) {
                    attrName = token.substring(p + 6);
                } else {
                    attrName = token;
                }
                if (prevPath.length() == 0) {
                    qry += "<Predicate AttributeName=\"" + attrName + "\" Operand=\"EQ\" URL_ID=\"" + centricRef
                            + "\"/>";
                    prevPath = token;
                } else {
                    qry += "<Predicate AttributeName=\"" + attrName + "\" Operand=\"EQ\" URL_ID=\"" + centricRef
                            + "\" Path=\"" + prevPath + "\"/>";
                    prevPath += "/" + token;
                }
            }
            qry += "</OR>";
        } else if (path != null) {
            qry += "<Predicate AttributeName=\"" + name + "\" Operand=\"EQ\" ValueString=\"" + CommonUtil.escapeUrl(value)
                    + "\" Path=\"" + path + "\"/>";
        } else {
            qry += "<Predicate AttributeName=\"" + name + "\" Operand=\"EQ\" ValueString=\"" + CommonUtil.escapeUrl(value)
                    + "\"/>";
        }
        return qry;
    }

    private List<String> tokenize(String value, String delimiter) {
        if (value == null) {
            return null;
        }
        List<String> tokenList = new ArrayList<String>();

        StringTokenizer st = new StringTokenizer(value, delimiter);
        while (st.hasMoreTokens()) {
            tokenList.add(st.nextToken());
        }

        return tokenList;
    }

    public String getUrlFromQuery(String query) {
        String url = null;
        urlId = null;

        String qry = query + "</AND></QUERY>";
        String detail = "<EXTRACT><DETAIL AttributeName=\"Node Name\" Seq=\"1\"/></EXTRACT>";
        ResultSet rs = q.getData(qry, detail);
        if (rs == null) {
            q.closeQuery();
            return null;
        }
        try {
            if (rs.next()) {
                urlId = rs.getString(2);
            }
        } catch (Exception e) {
        }
        q.closeQuery(rs);
        if (urlId != null) {
            url = q.getURL(urlId);
        }

        return url;
    }

    /**
     *
     * @param list
     * @param spec
     * @return
     * @author GHUANG
     * @version 2019年9月25日 上午11:23:04
     */
    public static String getExtractXML(ArrayList<HashMap<String, String>> list, String spec) {
        StringBuffer extractXML = new StringBuffer();
        extractXML.append("<EXTRACT>\n");
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                HashMap<String, String> map = list.get(i);
                String path = map.get("path");
                String seq = map.get("seq");
                if (path.length() > 0) {
                    path = " Path=\"" + path + "\"";
                } else {
                    path = "";
                }
                extractXML.append(" <DETAIL AttributeName=\"" + map.get("name") + "\" " + path + " Seq=\"" + seq +
                        "\"/>\n");
            }
        }

        extractXML.append(spec);
        extractXML.append("</EXTRACT>\n");
        return extractXML.toString();
    }

    /**
     *
     * @param nodetype
     * @param list
     * @param query
     * @param order
     * @return
     * @author GHUANG
     * @version 2019年9月25日 上午11:22:49
     */
    public static ArrayList<String> getQueryXML(String nodetype, ArrayList<HashMap<String, String>> list, String query,
                                                String order) {
        // TODO Auto-generated method stub
        ArrayList<String> querylist = new ArrayList();
        ArrayList<String> urlxmlist = queryURLXML(list);
        for (String urlxml : urlxmlist) {
            StringBuffer queryXML = new StringBuffer();
            queryXML.append("<QUERY>\n");
            queryXML.append("   <AND>\n");
            queryXML.append("       <Predicate NodeType=\"" + nodetype + "\" Operand=\"EQ\"/>\n");
            if (query.length() > 0) {
                queryXML.append(query);
            }
            queryXML.append("   <OR>\n");
            queryXML.append(urlxml);
            queryXML.append("   </OR>\n");
            queryXML.append("   </AND>\n");
            if(order!=null){
                queryXML.append(order);
            }
            queryXML.append("</QUERY>\n");
            querylist.add(queryXML.toString());
            queryXML = null;
        }
        urlxmlist = null;
        return querylist;
    }

    public static ArrayList queryURLXML(ArrayList<HashMap<String, String>> list) {
        ArrayList<String> qlist = new ArrayList();
        StringBuffer queryXML = new StringBuffer();
        int j = 0;
        log.info("{},list={}",list.size() , list);
        for (int i = 0; i < list.size(); i++, j++) {
            HashMap<String, String> map = list.get(i);
            String path = map.get("path");
            String attrkey = map.get("key");
            String attrvalue = map.get("value");
            String attrtype = map.get("type");
            if (attrtype.equalsIgnoreCase("string")) {
                queryXML.append(" <Predicate Path=\"" + path + "\" AttributeName=\"" + attrkey
                        + "\" Operand=\"EQ\" ValueString=\""
                        + attrvalue
                        + "\" />\n");
            } else if (attrtype.equalsIgnoreCase("ref")) {
                queryXML.append(" <Predicate Path=\"" + path + "\" AttributeName=\"" + attrkey
                        + "\" Operand=\"EQ\" ValueRef=\""
                        + attrvalue
                        + "\" />\n");
            } else if (attrtype.equalsIgnoreCase("double")) {
                queryXML.append(" <Predicate Path=\"" + path + "\" AttributeName=\"" + attrkey
                        + "\" Operand=\"EQ\" ValueNumber=\""
                        + attrvalue
                        + "\" />\n");
            } else if (attrtype.equalsIgnoreCase("url")) {
                queryXML.append("<Predicate NodeURL=\"" + attrvalue + "\" Operand=\"EQ\" />");
            }
            if (j > querylimit && queryXML.length() > 0) {
                qlist.add(queryXML.toString());
                j = 0;
                queryXML = new StringBuffer();
            }
            map = null;
        }
        if (j > 0 && queryXML.length() > 0) {
            qlist.add(queryXML.toString());

        }
        queryXML = null;
        return qlist;
    }

    /**
     * 按属性汇总
     *
     * @param rs
     * @param prefix
     * @param configInfo
     * @return
     * @author GHUANG
     * @version 2019年9月25日 上午11:22:43
     */
    public TreeMap<Integer, HashMap<String, ArrayList<String>>> getResultMap(ResultSet rs,
                                                                             String prefix, String configInfo) {
        TreeMap<Integer, HashMap<String, ArrayList<String>>> resultMap = new TreeMap<Integer, HashMap<String, ArrayList<String>>>();
        try {
            // outInfo("rs.fetchSize="+rs.getFetchSize());
            int count = 0;
            while (rs.next()) {

                String seq = NodeUrl.getStringValue(rs.getObject("SEQ"));
                int intseq = Integer.parseInt(seq);
                // 需要保证第一列不为空或者最后一列不为空
                if (intseq == 1) {
                    count = 0;
                }
                String typestr = properties.getValue(prefix + seq + ".type", "string");
                String attributeName = NodeUrl.getStringValue(rs.getObject("NAME"));
                String startID = NodeUrl.getStringValue(rs.getObject("START_ID"));
                String urlValue = NodeUrl.getStringValue(rs.getObject("VALUEURL_ID"));
                String strValue = NodeUrl.getStringValue(rs.getObject("VALUESTRING"));
                if (strValue == null) {
                    strValue = "";
                }
                // CSILog.outInfo(seq + ",startid=" + startID + ",strvalue=" + strValue, "test");
                if (typestr.equals("enum")) {
                    strValue = NodeUtil.getEnumEnDisplay(strValue);
                } else if (typestr.equals("time")) {
                    strValue = CommonUtil.parseTime(strValue, "yyyy-MM-dd");
                }

                if (typestr.startsWith("url")) {
                    if (typestr.indexOf(":") != -1) {
                        String path = typestr.substring(typestr.indexOf(":") + 1);
                        if (!urlValue.equals("1")) {
                            strValue = NodeUtil.queryExpressionResult(path, q.getURL(urlValue));
                        }
                    } else {
                        if (urlValue.equals("1")) {
                            strValue = "centric:";
                        } else {
                            strValue = q.getURL(urlValue);
                        }
                    }
                } else if (typestr.equals("startid")) {
                    strValue = startID;
                }

                if (!resultMap.containsKey(Integer.parseInt(seq))) {
                    HashMap<String, ArrayList<String>> valueMap = new HashMap<String, ArrayList<String>>();
                    String attName = attributeName;
                    valueMap.put("string", NodeUrl.getArrayList(strValue));
                    resultMap.put(Integer.parseInt(seq), valueMap);
                    // outInfo("attname-="+attName,configInfo);
                } else {
                    HashMap<String, ArrayList<String>> valueMap = resultMap.get(Integer.parseInt(seq));
                    String attName = attributeName;
                    ArrayList al2 = valueMap.get("string");
                    if (al2 == null) {
                        al2 = new ArrayList();
                    }

                    if (intseq - count == 1) {
                        count = intseq;
                        // al2.add("");
                    } else if (intseq - count > 1) {
                        int roll = intseq - count;
                        log.info("intseq={},count={}",intseq,count);
                        for (int r = 1; r < roll; r++) {
                            if (!resultMap.containsKey(count + r)) {
                                HashMap<String, ArrayList<String>> specvalueMap = new HashMap<String, ArrayList<String>>();
                                valueMap.put("string", NodeUrl.getArrayList(""));
                                resultMap.put(count + r, valueMap);
                            } else {
                                HashMap<String, ArrayList<String>> specvalueMap = resultMap.get(count + r);
                                ArrayList al1 = specvalueMap.get("string");
                                al1.add("");
                                specvalueMap.put("string", al1);
                            }
                        }
                        count = intseq;
                    }

                    al2.add(strValue);
                    valueMap.put("string", al2);
                }
            }
        } catch (Exception e) {
            log.error(configInfo, e);
        }
        return resultMap;
    }

    public static ArrayList<String> getArrayList(String seq1) {
        ArrayList<String> al = new ArrayList<String>();
        al.add(seq1);
        return al;
    }

    public static String getStringValue(Object obj) {
        String resultStr = "";
        if (obj == null) {
            resultStr = "";
        } else {
            resultStr = obj.toString();
        }

        return resultStr.trim();
    }

    /**
     * return list
     *
     * @param rs
     * @param prefix
     * @return
     * @author GHUANG
     * @version 2019年10月16日 下午11:37:25
     */
    public LinkedHashMap<String, ArrayList<String>> getDataResultMap(ResultSet rs, String prefix) {
        LinkedHashMap<String, ArrayList<String>> objmap = new LinkedHashMap();
        try {
            int count = 0;
            while (rs.next()) {
                String seq = NodeUrl.getStringValue(rs.getObject("DetailSeq"));
                int intseq = Integer.parseInt(seq);
                if (intseq == 1) { // 到第一列进行重置
                    count = 0;
                }
                String typestr = properties.getValue(prefix + seq + ".type", "string");
                String attributeName = NodeUrl.getStringValue(rs.getObject("AttributeName"));
                String startID = NodeUrl.getStringValue(rs.getObject("Node_URL_ID"));
                ArrayList<String> objlist = new ArrayList();
                String idurl = q.getURL(startID);
                if (objmap.containsKey(idurl)) {
                    objlist = objmap.get(idurl);
                }
                if (intseq - count == 1) {
                    count = intseq;
                    // al2.add("");
                } else if (intseq - count > 1) {
                    int roll = intseq - count;
                    // CSILog.outInfo(",intseq=" + intseq + ",count=" + count, "test");
                    for (int r = 1; r < roll; r++) {
                        objlist.add(""); // 为空的列补
                    }
                    count = intseq;
                }
                String urlValue = NodeUrl.getStringValue(rs.getObject("URL_ID"));
                String numberValue = getStringValue(rs.getObject("ValueNumber"));
                String strValue = "";
                String sValue = NodeUrl.getStringValue(rs.getObject("ValueString"));
                if (sValue == null) {
                    strValue = "";
                }

                if (typestr.equals("enum")) {
                    // CSILog.outInfo("firstenum=" + sValue, "test");
                    strValue = NodeUtil.getEnumEnDisplay(sValue);
                    // CSILog.outInfo("secondenum=" + strValue, "test");
                } else if (typestr.equals("time")) {
                    String format = properties.getValue(prefix + seq + ".format", "string");
                    strValue = CommonUtil.parseTime(sValue, format);
                } else if (typestr.equals("int")) {
                    if (strValue == null || strValue.equals("")) {
                        strValue = "0";
                    } else {
                        float ss = Float.parseFloat(strValue);
                        strValue = String.valueOf(Math.round(ss));
                    }
                } else if (typestr.equals("double")) {
                    if (numberValue == null || numberValue.equals("")) {
                        strValue = "0.0";
                    } else {
                        // 成本要求不控制精度
                        // String precision = CSProperties.getValue(prefix + seq + ".format", "2");
                        // BigDecimal db = new BigDecimal(numberValue);
                        // strValue = String.valueOf(
                        // db.setScale(Integer.parseInt(precision), BigDecimal.ROUND_HALF_UP).doubleValue());
                        strValue = numberValue;
                    }
                } else if (typestr.startsWith("url")) {
                    if (typestr.indexOf(":") != -1) {
                        String path = typestr.substring(typestr.indexOf(":") + 1);
                        if (!urlValue.equals("1")) {
                            strValue = NodeUtil.queryExpressionResult(path, q.getURL(urlValue));
                        }
                    } else {
                        if (urlValue.equals("1")) {
                            strValue = "centric:";
                        } else {
                            strValue = q.getURL(urlValue);
                        }
                    }
                } else if (typestr.equals("startid")) {
                    strValue = startID;
                } else if (typestr.equals("reflist")) {
                    log.info(typestr + strValue + ",,,urlValue=" + urlValue);

                } else {
                    strValue = sValue;
                }
                objlist.add(strValue);
                objmap.put(idurl, objlist);
                objlist = null;
            }

            rs = null;
            log.info("db search {}" , objmap);
        } catch (Exception e) {
            log.error("数据查询异常,异常方法NodeUrl.getDataResultMap", e);
        }
        return objmap;
    }

    /**
     * return attribute map
     *
     * @param rs
     * @param prefix
     * @param configInfo
     * @return
     * @author GHUANG
     * @version 2019年10月16日 下午11:37:25
     */
    public LinkedHashMap<String, HashMap<String, String>> getDataResultMapAttr(ResultSet rs, String prefix,
                                                                               String configInfo) {
        LinkedHashMap<String, HashMap<String, String>> objmap = new LinkedHashMap();
        try {
            int count = 0;
            while (rs.next()) {
                String seq = NodeUrl.getStringValue(rs.getObject("DetailSeq"));
                int intseq = Integer.parseInt(seq);
                if (intseq == 1) { // 到第一列进行重置
                    count = 0;
                }
                String typestr = properties.getProperty(prefix + seq + ".type", "string");
                String key = properties.getProperty(prefix + seq + ".key", seq);
                String attributeName = NodeUrl.getStringValue(rs.getObject("AttributeName"));
                String startID = NodeUrl.getStringValue(rs.getObject("Node_URL_ID"));
                HashMap<String, String> objlist = new HashMap();
                if (objmap.containsKey(q.getURL(startID))) {
                    objlist = objmap.get(q.getURL(startID));
                }
                if (intseq - count == 1) {
                    count = intseq;
                    // al2.add("");
                } else if (intseq - count > 1) {
                    int roll = intseq - count;
                    // CSILog.outInfo(",intseq=" + intseq + ",count=" + count, "test");
                    for (int r = 1; r < roll; r++) {
                        objlist.put(key, ""); // 为空的列补
                    }
                    count = intseq;
                }
                String urlValue = NodeUrl.getStringValue(rs.getObject("URL_ID"));
                String strValue = "";
                String numberValue = getStringValue(rs.getObject("ValueNumber"));
                String sValue = NodeUrl.getStringValue(rs.getObject("ValueString"));
                if (sValue == null) {
                    strValue = "";
                }
                // CSILog.outInfo("startID=" + startID + "urlValue=" + urlValue + ",strValue=" + strValue, DBQUERY);
                if (typestr.equals("enum")) {
                    // CSILog.outInfo("firstenum=" + sValue, "test");
                    strValue = NodeUtil.getEnumEnDisplay(sValue);
                    // CSILog.outInfo("secondenum=" + strValue, "test");
                } else if (typestr.equals("time")) {
                    String format = properties.getProperty(prefix + seq + ".format", "string");
                    strValue = CommonUtil.parseTime(sValue, format);
                } else if (typestr.equals("int")) {
                    if (strValue == null || strValue.equals("")) {
                        strValue = "0";
                    } else {
                        float ss = Float.parseFloat(strValue);
                        strValue = String.valueOf(Math.round(ss));
                    }
                } else if (typestr.equals("double")) {
                    if (StrUtil.isBlank(numberValue)) {
                        strValue = "0.0";
                    } else {
                        String precision = properties.getProperty(prefix + seq + ".format", "2");
                        BigDecimal db = new BigDecimal(numberValue);
                        strValue = String.valueOf(db.setScale(Integer.parseInt(precision), BigDecimal.ROUND_HALF_UP).doubleValue());
                    }
                } else if (typestr.startsWith("url")) {
                    // CSILog.outInfo("urlValue=" + urlValue, "test");
                    if (typestr.indexOf(":") != -1) {
                        String path = typestr.substring(typestr.indexOf(":") + 1);
                        if (!urlValue.equals("1")) {
                            // CSILog.outInfo("typestr=" + path, "test");
                            strValue = NodeUtil.queryExpressionResult(path, q.getURL(urlValue));
                        }
                    } else {
                        if (urlValue.equals("1")) {
                            strValue = "centric:";
                        } else {
                            strValue = q.getURL(urlValue);
                        }
                    }
                } else if (typestr.equals("startid")) {
                    strValue = startID;
                } else {
                    strValue = sValue;
                }
                objlist.put(key, strValue);
                objmap.put(q.getURL(startID), objlist);
            }
            rs = null;
            // CSILog.outInfo("db search " + objmap, "dbsearch");
        } catch (Exception e) {
            // TODO: handle exception
            log.error(configInfo, e);
        }
        return objmap;
    }

}
