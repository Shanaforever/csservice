/**
* @author GHUANG
* @version 2019年12月28日 下午8:02:42
*
*/
package com.centricsoftware.commons.utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.RowSet;

import com.centricsoftware.config.entity.CsProperties;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HSEUtil {
    private static final Logger log = LoggerFactory.getLogger(HSEUtil.class);
    public static HashMap<String, String> typemap = null;

    public static HashMap<String, String> attrmap = null;
    public static CsProperties CSProperties;
    static{
        CSProperties = NodeUtil.getProperties();
    }
    /**
     * ED BO对象表 ER BO存在多值属性且为ref类型 EN BO存在多值属性且为数字类型 ET BO存在多值属性且为String类型
     */
    static {
        attrmap = new HashMap();
        attrmap.put("__Parent__", "the_parent_id");
        attrmap.put("Node Name", "node_name");
        attrmap.put("Node Type", "node_type");
        attrmap.put("URL", "the_cnl");
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        // System.out.println(camel4underline("C8_User_TRApprover"));
        try {
            System.out.println(camel4underline(
                    "PartMaterial:__Parent__@Child,ApparelBOMRevision:__Parent__@PartMaterial,ApparelBOM:__Parent__@ApparelBOMRevision,Style:__Parent__@ApparelBOM"));

            HashMap colattrmap = new HashMap();
            colattrmap.put("Child", "__Parent__,TrackingColor");
            colattrmap.put("PartMaterial:__Parent__@Child#PartMaterial", "Node Name_0");
            HashMap<String, String> resultmap = HSEUtil.queryAttributesbyUrls("C123", "",
                    "PartMaterialTracking", colattrmap,
                    null);
            System.out.println("result=" + resultmap);
            getMultiAttribute("C0/673276478|Style", "433738", "Style", "BOMMainMaterials", "ref", null);
            // getSingleAttribute("C0/673276478|Style", "433738", "C8_SA_AFSStatus", "Style", "", null);
            getSingleAttribute("C0/673276478|Style", "433738", "Node Name", "Style",
                    "Category2:Category2,Category1:Category1,Season:ParentSeason", null);
            getSingleAttribute("", "75276", "Node Name", "ApparelBOM", "", null);
            getSingleAttribute("", "75276", "Node Name", "ApparelBOM", "Style:__Parent__", null);
            HashMap pmap = new HashMap();
            pmap.put("Category2:Category2", camel4underline("Node Name") + "_0");
            pmap.put("Category1:Category1", camel4underline("Node Name") + "_1");
            pmap.put("Category1:Category1,Season:ParentSeason",
                    camel4underline("Node Name") + "_2," + camel4underline("Code") + "_3");
            pmap.put("Child", camel4underline("Node Name"));
            HashMap map = new HashMap();
            map.put("Code", "123");
            queryAttributes("Style", pmap, map, null);
            queryAttributesWithConfbyUrl("C0/673276478|Style", "", "PartMaterial", "cs.view.bomview.item.", "",
                    "where a",
                    null);
        } catch (Exception e) {

        }
    }

    /**
     * 驼峰转下划线
     *
     * @param data
     * @return
     */
    public static String camel4underline(String data) {
        List record = new ArrayList();
        String result = "";
        if (attrmap.containsKey(data)) {
            return attrmap.get(data);
        }
        for (Map.Entry<String, String> m : attrmap.entrySet()) {
            String rep = m.getValue();
            String key = m.getKey();
            data = data.replaceAll(key, rep);
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        StringBuffer sb = new StringBuffer(data);
        int dl = data.length();
        for (int ii = 1; ii < dl - 1; ii++) {
            if (Character.isLowerCase(sb.charAt(ii)) && Character.isUpperCase(sb.charAt(ii + 1))) {
                sb.insert(ii + 1, '_');
                ii++;
                dl++;
                continue;
            }
            String numstr = Character.toString(sb.charAt(ii));
            Matcher isNum = pattern.matcher(numstr);
            if (isNum.matches() && Character.isUpperCase(sb.charAt(ii + 1))) {
                sb.insert(ii + 1, '_');
                ii++;
                dl++;
                continue;
            }

            if (Character.isUpperCase(sb.charAt(ii)) && Character.isLowerCase(sb.charAt(ii + 1))) {
                if ('_' == (sb.charAt(ii - 1)) || ',' == (sb.charAt(ii - 1)) || '@' == (sb.charAt(ii - 1))
                        || '#' == (sb.charAt(ii - 1)) || ':' == (sb.charAt(ii - 1))) {
                    continue;
                } else {
                    sb.insert(ii, '_');
                    ii++;
                    dl++;
                }

            }
        }
        for (int iii = 0; iii < sb.length(); iii++) {
            if (Character.isUpperCase(sb.charAt(iii))) {
                sb.setCharAt(iii, (char) (sb.charAt(iii) + 32));
            }
        }
        result += sb.toString();

        return result;

    }

    /* *//**
          * 多属性查询数据 对象条件
          *
          * @param type
          * @param attribute
          *            targetattr@type:attr
          * @param conmap
          * @param multiType
          * @return
          * @author GHUANG
          * @version 2019年12月29日 下午12:12:09
          *//*
             * public static List<HashMap<String, String>> queryAttributes(String type, String attributes,
             * HashMap<String, String> conmap, DBUtil util) { String sql = "select a.id,"; RowSet rs = null;
             * List<HashMap<String, String>> rlist = new ArrayList(); try { List<String> list = null; if
             * (attributes.length() > 0) { list = Arrays.asList(attributes.split(",")); for (String attr : list) { sql
             * += "a." + camel4underline(attr) + ","; } }
             *
             * if (sql.endsWith(",")) { sql = sql.substring(0, sql.lastIndexOf(",")); } sql += " from " + "ed_" +
             * camel4underline(type) + " a where "; String condition = ""; for (Map.Entry<String, String> artMap :
             * conmap.entrySet()) { String key = artMap.getKey(); String value = artMap.getValue();
             *
             * condition += "a." + camel4underline(key) + "='" + value + "' and "; } if (condition.endsWith("and ")) {
             * condition = condition.substring(0, condition.lastIndexOf("and")); } sql += condition;
             *
             * if (sql.length() > 0) { CSILog.outInfo("ATTRIBUTS=" + sql, "HSE"); rs = util.query(sql);
             *
             * while (rs.next()) { HashMap<String, String> colmap = new HashMap(); for (String attrname : list) {
             * colmap.put(attrname, rs.getString(attrname)); } rlist.add(colmap); } rs.close(); } } catch (Exception e)
             * { CSILog.exceptionInfo("Exception", e); } finally { if (rs != null) { try { rs.close(); } catch
             * (SQLException s) { rs = null; } } } return rlist; }
             */

    /**
     * 根据配置文件进行查询 属性key 改为type+"_"+attr+"_" +seq数字 path为类型:path@+上级类型 spec=a.id,a.the_cnl
     *
     * @param url
     *            根据URL查询或id查询
     * @param id
     * @param type
     *            查询BO类型
     * @param key
     *            配置文件的key
     * @param spec
     *            提供查询属性的额外定义
     * @param specwhere
     *            提供where条件的额外定义
     * @param util
     * @return
     * @author GHUANG
     * @version 2020年6月2日 下午3:58:48
     */
    public static LinkedHashMap<String, HashMap<String, String>> queryAttributesWithConfbyUrl(String url, String id,
            String type, String key, String spec, String specwhere, DBUtil util) {
        LinkedHashMap<String, HashMap<String, String>> rmap = new LinkedHashMap();
        RowSet rs = null;
        try {
            String sql = "select a.the_cnl,";
            List<String> list = new ArrayList();
            String pathwhere = "";
            String attrsql = "";
            if (spec.length() > 0) {
                String specstr[] = spec.split(",");
                for (int s = 0; s < specstr.length; s++) {
                    if (specstr[s].indexOf(".") < 0) {
                        sql += "a." + specstr[s] + ",";
                        list.add(specstr[s]);
                    } else {
                        sql += specstr[s] + ",";
                        list.add(specstr[s].substring(specstr[s].indexOf(".") + 1));
                    }

                }
            }
            int qamount = Integer.parseInt(CSProperties.getValue(key + "hseamount", "0"));
            int r = 0;
            HashMap<String, String> typemap = new HashMap();
            ArrayList<String> onepathlist = new ArrayList();
            typemap.put("child", "a");
            for (int i = 1; i < qamount; i++) {
                String attrkeys = CSProperties.getValue(key + i + ".hseattrname", "");
                // System.out.println(key + i + ".hseattrname" + "=" + attrkeys);
                String path = CSProperties.getValue(key + i + ".hseattrpath", "");
                String attrbo = CSProperties.getValue(key + i + ".hsebotype", "");
                String multitype = CSProperties.getValue(key + i + ".hseisonly", "");

                // String value = "";

                path = camel4underline(path);
                // System.out.println(path);
                multitype = camel4underline(multitype);
                if (attrkeys.length() > 0) {
                    attrkeys = camel4underline(attrkeys);
                }
                attrbo = camel4underline(attrbo);
                if (!path.equalsIgnoreCase("Child")) {

                    List<String> pathlist = Arrays.asList(path.split(","));
                    int n = 0;
                    for (n = 0; n < pathlist.size(); n++) {
                        String p = pathlist.get(n);
                        String ptype = p.substring(0, p.indexOf(":"));
                        String pattr = p.substring(p.indexOf(":") + 1, p.lastIndexOf("@"));
                        String lasttype = p.substring(p.lastIndexOf("@") + 1);
                        String temppath = "";
                        String abbr = " p" + r + n;
                        if (typemap.containsKey(ptype) && !multitype.equalsIgnoreCase(ptype)) {
                            abbr = typemap.get(ptype);
                        } else {
                            typemap.put(ptype, " p" + r + n);
                        }
                        if (typemap.get(lasttype) == null) {
                            System.out.println(pathlist);
                        }
                        temppath = " LEFT JOIN ed_" + ptype + abbr + " ON " + typemap.get(lasttype) + "." + pattr
                                + " = " + abbr + ".id \r\n";
                        if (!onepathlist.contains(temppath)) {
                            onepathlist.add(temppath);
                            pathwhere += temppath;
                        }

                    }
                    if (attrkeys.length() > 0) {
                        if (attrkeys.contains(",")) {
                            List<String> attrkeylist = Arrays.asList(attrkeys.split(","));
                            for (String attrkey : attrkeylist) {
                                String attr = attrkey.substring(0, attrkey.lastIndexOf("_"));
                                // String atype = attrkey.substring(0, attrkey.indexOf("_"));
                                attrsql += typemap.get(attrbo) + "." + attr + " as " + attrkey + ",";
                                list.add(attrkey);
                            }
                        } else {
                            String attr = attrkeys.substring(0, attrkeys.lastIndexOf("_"));
                            // String atype = attrkeys.substring(0, attrkeys.indexOf("_"));
                            attrsql += typemap.get(attrbo) + "." + attr + " as " + attrkeys + ",";
                            list.add(attrkeys);

                        }
                    }
                    r++;
                } else {
                    if (attrkeys.length() > 0) {
                        if (attrkeys.contains(",")) {
                            List<String> attrkeylist = Arrays.asList(attrkeys.split(","));
                            for (String attrkey : attrkeylist) {
                                String attr = attrkey.substring(0, attrkey.lastIndexOf("_"));
                                attrsql += "a." + attr + " as " + attrkey + ",";
                                list.add(attrkey);
                            }
                        } else {
                            String attr = attrkeys.substring(0, attrkeys.lastIndexOf("_"));
                            attrsql += "a." + attr + " as " + attrkeys + ",";
                            list.add(attrkeys);
                        }
                    }
                }

            }

            sql += attrsql;
            if (sql.endsWith(",")) {
                sql = sql.substring(0, sql.lastIndexOf(","));
            }
            sql += " from " + "ed_" + camel4underline(type) + " a" + pathwhere;
            String where = "";
            if (url.length() > 0) {
                if (url.contains(",")) {
                    url = "'" + url.replace(",", "','") + "'";
                } else {
                    url = "'" + url + "'";
                }
                where = " where a.THE_CNL in (" + url + ")";
            } else if (id.length() > 0) {
                if (id.contains(",")) {
                    id = "'" + id.replace(",", "','") + "'";
                } else {
                    id = "'" + id + "'";
                }
                where = " where a.id in (" + id + ")";
            }
            if (specwhere.length() == 0) {
                sql += where;
            } else {
                sql += specwhere;
            }

            if (sql.length() > 0) {
                log.info("Config ATTRIBUTS PATH SQL=" + sql);
                rs = util.query(sql);
                log.info("ATTR list" + list);
                while (rs.next()) {
                    HashMap<String, String> colmap = new HashMap();
                    String thecnl = rs.getString("the_cnl");
                    for (String attrname : list) {
                        String value = rs.getString(attrname);
                        if (spec.contains(attrname)) {
                            colmap.put(attrname, value);
                        } else {
                            String seqkey = attrname.substring(attrname.lastIndexOf("_") + 1);
                            String attrtype = CSProperties.getValue(key + seqkey + ".hseattrtype", "");
                            if (value == null) {
                                // CSILog.outInfo("--null value--" + attrname, log);
                                value = "";
                            }
                            if (attrtype.equals("enum")) {
                                if (value.indexOf(":") > 0) {
                                    // value = getEnumValueZH(value, util);
                                    value = NodeUtil.getEnumEnDisplay(value);
                                }
                                colmap.put(seqkey, value);

                            } else {
                                colmap.put(seqkey, value);
                            }
                        }
                    }
                    rmap.put(thecnl, colmap);
                    colmap = null;

                }
                list = null;
                rs.close();
                rs = null;
            }

        } catch (Exception e) {
            log.error("Exception", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch (SQLException s) {
                    rs = null;
                }
            }
        }
        return rmap;
    }

    public static LinkedHashMap<String, HashMap<String, String>> queryAttributesWithConfbyId(String url, String id,
            String type, String key, String spec, String specwhere, DBUtil util) {
        LinkedHashMap<String, HashMap<String, String>> rmap = new LinkedHashMap();
        RowSet rs = null;
        try {
            String sql = "select a.id,";
            List<String> list = new ArrayList();
            String pathwhere = "";
            String attrsql = "";
            if (spec.length() > 0) {
                String specstr[] = spec.split(",");
                for (int s = 0; s < specstr.length; s++) {
                    if (specstr[s].indexOf(".") < 0) {
                        sql += "a." + specstr[s] + ",";
                        list.add(specstr[s]);
                    } else {
                        sql += specstr[s] + ",";
                        list.add(specstr[s].substring(specstr[s].indexOf(".") + 1));
                    }

                }
            }
            int qamount = Integer.parseInt(CSProperties.getValue(key + "hseamount", "0"));
            int r = 0;
            HashMap<String, String> typemap = new HashMap();
            ArrayList<String> onepathlist = new ArrayList();
            typemap.put("child", "a");
            for (int i = 1; i < qamount; i++) {
                String attrkeys = CSProperties.getValue(key + i + ".hseattrname", "");
                // System.out.println(key + i + ".hseattrname" + "=" + attrkeys);
                String path = CSProperties.getValue(key + i + ".hseattrpath", "");
                String attrbo = CSProperties.getValue(key + i + ".hsebotype", "");
                String multitype = CSProperties.getValue(key + i + ".hseisonly", "");

//                if (attrkeys == null || attrkeys.length() == 0) {
//                    continue;
//
//                }
                // String value = "";

                path = camel4underline(path);
                // System.out.println(path);
                multitype = camel4underline(multitype);
                if (attrkeys.length() > 0) {
                    attrkeys = camel4underline(attrkeys);
                }
                attrbo = camel4underline(attrbo);
                if (!path.equalsIgnoreCase("Child")) {

                    List<String> pathlist = Arrays.asList(path.split(","));
                    int n = 0;
                    for (n = 0; n < pathlist.size(); n++) {
                        String p = pathlist.get(n);
                        String ptype = p.substring(0, p.indexOf(":"));
                        String pattr = p.substring(p.indexOf(":") + 1, p.lastIndexOf("@"));
                        String lasttype = p.substring(p.lastIndexOf("@") + 1);
                        String temppath = "";
                        String abbr = " p" + r + n;
                        if (typemap.containsKey(ptype) && !multitype.equalsIgnoreCase(ptype)) {
                            abbr = typemap.get(ptype);
                        } else {
                            typemap.put(ptype, " p" + r + n);
                        }
                        if (typemap.get(lasttype) == null) {
                            System.out.println(pathlist);
                        }
                        temppath = " LEFT JOIN ed_" + ptype + abbr + " ON " + typemap.get(lasttype) + "." + pattr
                                + " = " + abbr + ".id \r\n";
                        if (!onepathlist.contains(temppath)) {
                            onepathlist.add(temppath);
                            pathwhere += temppath;
                        }

                    }
                    if (attrkeys.length() > 0) {
                        if (attrkeys.contains(",")) {
                            List<String> attrkeylist = Arrays.asList(attrkeys.split(","));
                            for (String attrkey : attrkeylist) {
                                String attr = attrkey.substring(0, attrkey.lastIndexOf("_"));
                                // String atype = attrkey.substring(0, attrkey.indexOf("_"));
                                attrsql += typemap.get(attrbo) + "." + attr + " as " + attrkey + ",";
                                list.add(attrkey);
                            }
                        } else {
                            String attr = attrkeys.substring(0, attrkeys.lastIndexOf("_"));
                            // String atype = attrkeys.substring(0, attrkeys.indexOf("_"));
                            attrsql += typemap.get(attrbo) + "." + attr + " as " + attrkeys + ",";
                            list.add(attrkeys);

                        }
                    }
                    r++;
                } else {
                    if (attrkeys.length() > 0) {
                        if (attrkeys.contains(",")) {
                            List<String> attrkeylist = Arrays.asList(attrkeys.split(","));
                            for (String attrkey : attrkeylist) {
                                String attr = attrkey.substring(0, attrkey.lastIndexOf("_"));
                                attrsql += "a." + attr + " as " + attrkey + ",";
                                list.add(attrkey);
                            }
                        } else {
                            String attr = attrkeys.substring(0, attrkeys.lastIndexOf("_"));
                            attrsql += "a." + attr + " as " + attrkeys + ",";
                            list.add(attrkeys);
                        }
                    }
                }

            }

            sql += attrsql;
            if (sql.endsWith(",")) {
                sql = sql.substring(0, sql.lastIndexOf(","));
            }
            sql += " from " + "ed_" + camel4underline(type) + " a" + pathwhere;
            String where = "";
            if (url.length() > 0) {
                if (url.contains(",")) {
                    url = "'" + url.replace(",", "','") + "'";
                } else {
                    url = "'" + url + "'";
                }
                where = " where a.THE_CNL in (" + url + ")";
            } else if (id.length() > 0) {
                if (id.contains(",")) {
                    id = "'" + id.replace(",", "','") + "'";
                } else {
                    id = "'" + id + "'";
                }
                where = " where a.id in (" + id + ")";
            }
            if (specwhere.length() == 0) {
                sql += where;
            } else {
                sql += specwhere;
            }

            if (sql.length() > 0) {
                log.info("Config ATTRIBUTS PATH SQL=" + sql);
                rs = util.query(sql);
                log.info("ATTR list" + list);
                while (rs.next()) {
                    HashMap<String, String> colmap = new HashMap();
                    String thecnl = rs.getString("id");
                    for (String attrname : list) {
                        String value = rs.getString(attrname);
                        if (spec.contains(attrname)) {
                            colmap.put(attrname, value);
                        } else {
                            String seqkey = attrname.substring(attrname.lastIndexOf("_") + 1);
                            String attrtype = CSProperties.getValue(key + seqkey + ".hseattrtype", "");
                            if (value == null) {
                                // CSILog.outInfo("--null value--" + attrname, log);
                                value = "";
                            }
                            if (attrtype.equals("enum")) {
                                if (value.indexOf(":") > 0) {
                                    // value = getEnumValueZH(value, util);
                                    value = NodeUtil.getEnumEnDisplay(value);
                                }
                                colmap.put(seqkey, value);

                            } else {
                                colmap.put(seqkey, value);
                            }
                        }
                    }
                    rmap.put(thecnl, colmap);
                    colmap = null;

                }
                list = null;
                rs.close();
                rs = null;
            }

        } catch (Exception e) {
            log.error("Exception", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch (SQLException s) {
                    rs = null;
                }
            }
        }
        return rmap;
    }

    /**
     * 按条件进行查询
     *
     * @param type
     *            对象BO
     * @param attrmap
     *            要获取对象属性的map path为key,获取属性用逗号,分隔,属性加BO类型# ,key为child则获取当前对象属性
     * @param conmap
     *            查询条件的map，仅限当前对象属性
     * @param util
     *            DBUtil
     * @return
     * @author GHUANG
     * @version 2020年5月2日 下午9:45:59
     */
    public static List<HashMap<String, String>> queryAttributes(String type, HashMap<String, String> attrmap,
            HashMap<String, String> conmap, DBUtil util) {
        String sql = "select a.id,";
        RowSet rs = null;
        List<HashMap<String, String>> rlist = new ArrayList();
        try {
            List<String> list = new ArrayList();
            String pathwhere = "";
            String attrsql = "";
            if (attrmap.size() > 0) {
                int r = 0;
                for (Map.Entry<String, String> map : attrmap.entrySet()) {
                    String path = map.getKey();
                    String attrkeys = map.getValue();
                    path = camel4underline(path);
                    attrkeys = camel4underline(attrkeys);

                    if (!path.equalsIgnoreCase("Child")) {

                        List<String> pathlist = Arrays.asList(path.split(","));
                        int n = 0;
                        for (n = 0; n < pathlist.size(); n++) {
                            String p = pathlist.get(n);
                            String ptype = p.substring(0, p.indexOf(":"));
                            String pattr = p.substring(p.indexOf(":") + 1);
                            String temppath = "";
                            if (n == 0) {
                                temppath = " LEFT JOIN ed_" + ptype + " p" + r + n + " ON a." + pattr + " = p" + r + n
                                        + ".id \r\n";
                            } else {
                                temppath = " LEFT JOIN ed_" + ptype + " p" + r + n + " ON p" + r + (n - 1) + "." + pattr
                                        + " = p" + r + n + ".id \r\n";
                            }
                            pathwhere += temppath;

                        }
                        if (attrkeys.contains(",")) {
                            List<String> attrkeylist = Arrays.asList(attrkeys.split(","));
                            for (String attrkey : attrkeylist) {
                                String attr = attrkey.substring(0, attrkey.lastIndexOf("_"));
                                attrsql += "p" + r + (n - 1) + "." + attr + " as " + attrkey + ",";
                                list.add(attrkey);
                            }
                        } else {
                            String attr = attrkeys.substring(0, attrkeys.lastIndexOf("_"));
                            attrsql += "p" + r + (n - 1) + "." + attr + " as " + attrkeys + ",";
                            list.add(attrkeys);
                        }
                        r++;
                    } else {
                        if (attrkeys.contains(",")) {
                            List<String> attrkeylist = Arrays.asList(attrkeys.split(","));
                            for (String attrkey : attrkeylist) {
                                attrsql += "a." + attrkey + ",";
                                list.add(attrkey);
                            }
                        } else {
                            attrsql += "a." + attrkeys + ",";
                            list.add(attrkeys);
                        }
                    }
                }

            } else {
                return rlist;
            }
            sql += attrsql;
            if (sql.endsWith(",")) {
                sql = sql.substring(0, sql.lastIndexOf(","));
            }
            sql += " from " + "ed_" + camel4underline(type) + " a" + pathwhere + "  where ";
            String condition = "";
            for (Map.Entry<String, String> artMap : conmap.entrySet()) {
                String key = artMap.getKey();
                String value = artMap.getValue();

                condition += "a." + camel4underline(key) + "='" + value + "' and ";
            }
            if (condition.endsWith("and ")) {
                condition = condition.substring(0, condition.lastIndexOf("and"));
            }
            sql += condition;

            if (sql.length() > 0) {
                log.error("ATTRIBUTS PATH SQL=" + sql);
                rs = util.query(sql);

                while (rs.next()) {
                    HashMap<String, String> colmap = new HashMap();
                    for (String attrname : list) {
                        colmap.put(attrname, rs.getString(attrname));
                    }
                    rlist.add(colmap);
                }
                // rs = null;
                conmap = null;
                rs.close();
                rs = null;
            }
        } catch (Exception e) {
            log.error("Exception", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch (SQLException s) {
                    rs = null;
                }
            }
        }
        return rlist;
    }

    public static List<HashMap<String, String>> queryBO(String type,
            HashMap<String, String> conmap, DBUtil util) {
        String sql = "select a.id,";
        RowSet rs = null;
        List<HashMap<String, String>> rlist = new ArrayList();
        try {
            List<String> list = new ArrayList();
            String pathwhere = "";

            sql += "select a.the_cnl from " + "ed_" + camel4underline(type) + " a" + pathwhere + "  where ";
            String condition = "";
            for (Map.Entry<String, String> artMap : conmap.entrySet()) {
                String key = artMap.getKey();
                String value = artMap.getValue();

                condition += "a." + camel4underline(key) + "='" + value + "' and ";
            }
            if (condition.endsWith("and ")) {
                condition = condition.substring(0, condition.lastIndexOf("and"));
            }
            sql += condition;

            if (sql.length() > 0) {
                log.info("ATTRIBUTS PATH SQL=" + sql);
                rs = util.query(sql);

                while (rs.next()) {
                    HashMap<String, String> colmap = new HashMap();
                    for (String attrname : list) {
                        colmap.put(attrname, rs.getString(attrname));
                    }
                    rlist.add(colmap);
                }
                // rs = null;
                conmap = null;
                rs.close();
                rs = null;
            }
        } catch (Exception e) {
            log.error("Exception", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch (SQLException s) {
                    rs = null;
                }
            }
        }
        return rlist;
    }

    /**
     *
     * @param url
     *            可以多个url
     * @param id
     *            可以多个id
     * @param type
     * @param attrmap
     *            属性map,格式路径
     * @param util
     * @return
     * @author GHUANG
     * @version 2020年6月2日 下午4:10:47
     */
    public static HashMap<String, String> queryAttributesbyUrls(String url, String id, String type,
            HashMap<String, String> attrmap,
            DBUtil util) {
        String sql = "select a.id,";
        RowSet rs = null;
        HashMap<String, String> colmap = new HashMap();
        try {
            List<String> list = new ArrayList();
            String pathwhere = "";
            String where = "";
            String attrsql = "";
            if (url.length() > 0) {
                if (url.contains(",")) {
                    url = "'" + url.replace(",", "','") + "'";
                } else {
                    url = "'" + url + "'";
                }
                where = " where a.THE_CNL in (" + url + ")";
            } else if (id.length() > 0) {
                if (id.contains(",")) {
                    id = "'" + id.replace(",", "','") + "'";
                } else {
                    id = "'" + id + "'";
                }
                where = " where a.id in (" + id + ")";
            } else {
                return null;
            }
            if (attrmap.size() > 0) {
                int r = 0;
                HashMap<String, String> typemap = new HashMap();
                ArrayList<String> onepathlist = new ArrayList();
                typemap.put("child", "a");
                for (Map.Entry<String, String> map : attrmap.entrySet()) {
                    String path = map.getKey();
                    String attrkeys = map.getValue();
                    path = camel4underline(path);
                    attrkeys = camel4underline(attrkeys);
                    if (!path.equalsIgnoreCase("Child")) {
                        String attrbo = path.substring(path.indexOf("#") + 1);
                        path = path.substring(0, path.indexOf("#"));
                        List<String> pathlist = Arrays.asList(path.split(","));
                        int n = 0;
                        for (n = 0; n < pathlist.size(); n++) {
                            String p = pathlist.get(n);
                            String ptype = p.substring(0, p.indexOf(":"));
                            String pattr = p.substring(p.indexOf(":") + 1, p.lastIndexOf("@"));
                            String lasttype = p.substring(p.lastIndexOf("@") + 1);
                            String temppath = "";
                            String abbr = " p" + r + n;
                            if (typemap.containsKey(ptype)) {
                                abbr = typemap.get(ptype);
                            } else {
                                typemap.put(ptype, " p" + r + n);
                            }
                            if (typemap.get(lasttype) == null) {
                                System.out.println(pathlist);
                            }
                            temppath = " LEFT JOIN ed_" + ptype + abbr + " ON " + typemap.get(lasttype) + "." + pattr
                                    + " = " + abbr + ".id \r\n";
                            if (!onepathlist.contains(temppath)) {
                                onepathlist.add(temppath);
                                pathwhere += temppath;
                            }

                        }
                        if (attrkeys.length() > 0) {

                            if (attrkeys.contains(",")) {
                                List<String> attrkeylist = Arrays.asList(attrkeys.split(","));
                                for (String attrkey : attrkeylist) {
                                    String attr = attrkey.substring(0, attrkey.lastIndexOf("_"));
                                    // String atype = attrkey.substring(0, attrkey.indexOf("_"));
                                    attrsql += typemap.get(attrbo) + "." + attr + " as " + attrkey + ",";
                                    list.add(attrkey);
                                }
                            } else {
                                String attr = attrkeys.substring(0, attrkeys.lastIndexOf("_"));
                                // String atype = attrkeys.substring(0, attrkeys.indexOf("_"));
                                attrsql += typemap.get(attrbo) + "." + attr + " as " + attrkeys + ",";
                                list.add(attrkeys);

                            }
                        }
                        r++;
                    } else {
                        if (attrkeys.length() > 0) {
                            if (attrkeys.contains(",")) {
                                List<String> attrkeylist = Arrays.asList(attrkeys.split(","));
                                for (String attrkey : attrkeylist) {
                                    String attr = attrkey.substring(0, attrkey.lastIndexOf("_"));
                                    attrsql += "a." + attr + " as " + attrkey + ",";
                                    list.add(attrkey);
                                }
                            } else {
                                String attr = attrkeys.substring(0, attrkeys.lastIndexOf("_"));
                                attrsql += "a." + attr + " as " + attrkeys + ",";
                                list.add(attrkeys);
                            }
                        }
                    }

                }

            } else {
                return null;
            }
            sql += attrsql;
            if (sql.endsWith(",")) {
                sql = sql.substring(0, sql.lastIndexOf(","));
            }
            sql += " from " + "ed_" + camel4underline(type) + " a" + pathwhere + " " + where;

            if (sql.length() > 0) {
                log.info("AttrURLS PATH SQL=" + sql);
                rs = util.query(sql);

                while (rs.next()) {
                    colmap.put("id", rs.getString("id"));
                    for (String attrname : list) {
                        colmap.put(attrname, rs.getString(attrname));
                    }
                }

                rs.close();
                rs = null;
            }
        } catch (Exception e) {
            log.error("Exception", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch (SQLException s) {
                    rs = null;
                }
            }
        }
        return colmap;
    }

    /**
     * 单个属性获取
     *
     * @param url
     * @param id
     * @param attribute
     * @param type
     * @param path
     *            Type:Attribute,Type2:Attribute2
     * @param util
     * @return
     * @author GHUANG
     * @version 2020年4月30日 上午11:43:37
     */
    public static String getSingleAttribute(String url, String id, String attribute, String type, String path,
            DBUtil util) {
        String value = "";
        RowSet rs = null;
        attribute = camel4underline(attribute);
        try {
            String where = "";
            if (url.length() > 0 && !url.equals("centric:")) {
                where = " where a.THE_CNL = '" + url + "'";
            } else if (id.length() > 0 && !id.equals("1")) {
                where = " where a.id = '" + id + "'";
            } else {
                return "";
            }
            String pathwhere = "";
            String attr = "";
            int n = 0;
            if (path.length() > 0) {
                List<String> pathlist = Arrays.asList(path.split(","));
                for (n = 0; n < pathlist.size(); n++) {
                    String p = pathlist.get(n);
                    if (p.length() == 0) {
                        continue;
                    }
                    String ptype = camel4underline(p.substring(0, p.indexOf(":")));
                    String pattr = camel4underline(p.substring(p.indexOf(":") + 1));
                    String temppath = "";
                    if (n == 0) {
                        temppath = " LEFT JOIN ed_" + ptype + " p" + n + " ON a." + pattr + " = p" + n
                                + ".id \r\n";
                        attr = "p0";
                    } else {
                        temppath = " LEFT JOIN ed_" + ptype + " p" + n + " ON p" + (n - 1) + "." + pattr
                                + " = p" + n + ".id \r\n";
                        attr = "p" + (n - 1);
                    }
                    pathwhere += temppath;

                }
            } else {
                attr = "a";
            }
            String sql = "select " + attr + "." + attribute + " from ed_" + camel4underline(type)
                    + " a"
                    + pathwhere + where;
            log.info("SINGLE&expres=" + sql);
            rs = util.query(sql);
            while (rs.next()) {
                value = rs.getString(attribute);
            }
            rs.close();
        } catch (Exception e) {
            log.error("Exception", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch (SQLException s) {
                    rs = null;
                }
            }
        }
        return value;
    }

    /**
     * 从 id 获取URL
     *
     * @param id
     * @param type
     * @param path
     * @param util
     * @return
     * @author GHUANG
     * @version 2020年4月30日 上午9:41:30
     */
    public static String getBOUrlbyID(String id, String type, String path, DBUtil util) {
        if (id.length() == 0) {
            return null;
        }
        if (id.equals("1")) {
            return "centric:";
        }
        String value = "";
        RowSet rs = null;
        try {
            String sql = "select the_cnl from ed_" + camel4underline(type)
                    + " where id = '" + id + "'";
            log.info("BO=" + sql);
            rs = util.query(sql);
            while (rs.next()) {
                value = rs.getString("the_cnl");
            }
            rs.close();
            rs = null;
        } catch (Exception e) {
            log.error("Exception", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch (SQLException s) {
                    rs = null;
                }
            }
        }
        return value;
    }

    /**
     * 多值属性获取
     *
     * @param url
     * @param ID
     * @param botype
     * @param attribute
     * @param attrtype
     * @param util
     * @return
     * @author GHUANG
     * @version 2020年4月30日 上午11:43:12
     */
    public static String getMultiAttribute(String url, String ID, String botype, String attribute,
            String attrtype, DBUtil util) {
        String sql = "select ";
        RowSet rs = null;
        String table = "";
        botype = camel4underline(botype);
        // attribute = camel4underline(attribute);
        String id_sql = "";
        String value = "";

        try {
            String mtable = "ed_" + botype;
            String leftjoin = "";
            if (ID.length() > 0) {
                id_sql = " and a.id='" + ID + "'";
            } else if (url.length() > 0) {
                leftjoin = "left join " + mtable + " b on a.id=b.id ";
                id_sql = "and b.the_cnl='" + url + "'";
            }
            if (attrtype.equals("ref")) {
                table = "er_" + botype;
                sql += " a.ref_id from " + table + " a  " + leftjoin + " where  a.attr_id='"
                        + attribute + "' " + id_sql + " order by a.map_key";
                ;

            } else if (attrtype.equals("string")) {
                table = "et_" + botype;
                sql += " a.text_value from " + table + " a " + leftjoin + " where a.attr_id='" + attribute + "' "
                        + id_sql
                        + "+ order by a.map_key";
            } else if (attrtype.equals("number")) {
                table = "en_" + botype;
                sql += " a.num_value from " + table + " a " + leftjoin + " where a.attr_id='" + attribute + "' "
                        + id_sql
                        + " order by a.map_key";
            }
            log.info("MULTI=" + sql);
            if (sql.length() > 0) {
                rs = util.query(sql);
            }
            while (rs.next()) {
                if (attrtype.equals("ref")) {
                    value += rs.getString("ref_id") + ",";
                } else if (attrtype.equals("number")) {
                    value += rs.getString("num_value") + ",";
                } else if (attrtype.equals("string")) {
                    value += rs.getString("text_value") + ",";
                }
            }
            rs.close();
            rs = null;
        } catch (Exception e) {
            log.error("Exception", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch (SQLException s) {
                    rs = null;
                }
            }
        }
        if (value.endsWith(",")) {
            value = value.substring(0, value.lastIndexOf(","));
        }
        return value;
    }

    /**
     * 获取Enum值
     *
     * @param attribute
     * @return
     * @author GHUANG
     * @version 2019年12月29日 下午4:46:23
     */
    public static String getEnumValueZH(String attribute, DBUtil util) {
        RowSet rs = null;
        String value = "";
        try {
            String sql = "select a.text_value from et_locale_configuration a where a.map_key='" + attribute
                    + "' and id in(select id from ed_locale_configuration b where b.node_name='zh')";
            log.info("ENUM SEARCH=" + sql);
            if (sql.length() > 0) {
                rs = util.query(sql);
            }

            while (rs.next()) {
                value = rs.getString(1);
            }
            rs.close();
            rs = null;
        } catch (Exception e) {
            log.error("Exception", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch (SQLException s) {
                    rs = null;
                }
            }
        }
        return value;
    }

}
