package com.centricsoftware.commons.utils;

import com.centricsoftware.config.entity.CsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

@Slf4j
public class CommonUtil {
    public static String ROOTFOLDER;

    public static String TEMPFOLDER;

    public static String SHAREFOLDER;

    public static String TEMPLATEFOLDER;
    static {
        CsProperties properties = NodeUtil.getProperties();
        ROOTFOLDER = properties.getValue("cs.rootfolder");
        TEMPLATEFOLDER = ROOTFOLDER + "template\\";
        TEMPFOLDER = ROOTFOLDER + "temp\\";
        SHAREFOLDER = ROOTFOLDER + "share\\";
    }


    /**
     * 转换timestamp
     *
     * @param attrvalue
     * @param attrformat
     * @param CLASSNAME
     * @return
     * @author GHUANG
     * @version 2019年6月10日 上午11:56:16
     */
    public static String parseTimestamp(String attrvalue, String attrformat, String CLASSNAME) {
        DateFormat format = new SimpleDateFormat(attrformat);
        String svalue = "";
        try {
            Timestamp ts = new Timestamp(format.parse(attrvalue).getTime());
            long tsvalue = ts.getTime() / 1000;
            svalue = String.valueOf(tsvalue);
        } catch (Exception e) {
            log.error(CLASSNAME, e);
        }
        return svalue;
    }

    public static String getCurrentDate(String format) {
        SimpleDateFormat sm = new SimpleDateFormat(format);
        return sm.format(new Date());
    }

    public static String createTimeNo() {
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyyMMdd");
        fullDateFormat.setTimeZone(java.util.TimeZone.getTimeZone("GMT+8:00"));
        String currentTime = fullDateFormat.format(new Date(System.currentTimeMillis()));
        return currentTime;
    }

    public static String createTime() {
        Timestamp d = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 定义格式
        return df.format(d);
    }

    public static String createCurrentTimestamp(String CLASSNAME) {
        String svalue = "";
        try {
            Timestamp d = new Timestamp(System.currentTimeMillis());
            long tsvalue = d.getTime() / 1000;
            svalue = String.valueOf(tsvalue);
        } catch (Exception e) {
            log.error(CLASSNAME, e);
        }
        return svalue;
    }

    public static HashMap changeStr2Map(String mapStr) {
        HashMap<String, String> map = new HashMap<String, String>();
        String cms = mapStr.replace("{", "").replace("}", "");
        String[] mapStrs = cms.split(",");
        for (String s : mapStrs) {
            String[] ms = s.split("=");
            System.out.println(s);
            if (ms.length == 2) {
                map.put(ms[0], ms[1]);
            }

        }
        return map;
    }

    public static String escapeUrl(String value) {
        if (value == null) {
            return "";
        }
        String s = value.replaceAll("&", "&amp;");
        s = s.replaceAll("<", "&lt;");
        s = s.replaceAll(">", "&gt;");
        s = s.replaceAll("\"", "&quot;");
        s = s.replaceAll("'", "&apos;");

        return s;

    }

    /**
     * 拆分List
     *
     * @param list
     * @param len
     * @return
     * @author GHUANG
     * @version 2019年11月5日 下午3:35:49
     */
    public static List splitList(List list, int len) {
        List result = new ArrayList();
        log.info("input list={},core={}",list,len);
        if (list == null || list.size() == 0 || len < 1) {
            result.add(list);
        } else {

            int size = list.size();
            int count = (size + len - 1) / len;

            for (int i = 0; i < count; i++) {
                List subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
                result.add(subList);
            }
        }
        return result;
    }

    /**
     * 拆分List
     *
     * @param map
     * @param len
     * @return
     * @author GHUANG
     * @version 2019年11月5日 下午3:35:49
     */
    public static List splitMap(LinkedHashMap<String, ArrayList<String>> map, int len) {
        List result = new ArrayList();
        if (map == null || map.size() == 0 || len < 1) {
            result.add(map);
        } else {

            int size = map.size();
            int count = (size + len - 1) / len;
            for (int i = 0; i < count; i++) {
                int fromIndex = i * len;
                int toIndex = ((i + 1) * len > size ? size : len * (i + 1));
                LinkedHashMap<String, ArrayList<String>> newmap = new LinkedHashMap<String, ArrayList<String>>();
                int j = 0;
                for (Entry<String, ArrayList<String>> entry : map.entrySet()) {
                    if (j >= fromIndex && j < toIndex) {
                        newmap.put(entry.getKey(), entry.getValue());
                    }
                    j++;
                }
                result.add(newmap);
            }
        }
        return result;
    }

    public static String escapeSpecUrl(String value) {
        if (value == null) {
            return "";
        }
        String s = value.replaceAll("&", "&amp;");
        s = s.replaceAll("\"", "&quot;");
        s = s.replaceAll("'", "&apos;");

        // s = stripBadChar(s);
        return s;

    }

    public static String getExtractXML(ArrayList<HashMap<String, String>> list, String spec) {
        String extractXML = "";
        extractXML += "<EXTRACT>\n";
        for (int i = 0; i < list.size(); i++) {
            HashMap<String, String> map = list.get(i);
            String path = map.get("path");
            if (path.length() > 0) {
                path = " Path=\"" + path + "\"";
            } else {
                path = "";
            }
            extractXML += " <DETAIL AttributeName=\"" + map.get("name") + "\" " + path + " Seq=\""
                    + (i + 1) + "\"/>\n";
        }
        if (extractXML.length() > 0) {
            extractXML += spec;
        }
        extractXML += "</EXTRACT>\n";
        return extractXML;
    }

    public static String getQueryXML(String nodetype, ArrayList<HashMap<String, String>> list, String notcondition,
            String order) {
        // TODO Auto-generated method stub
        String queryXML = "";
        queryXML += "<QUERY>\n";
        queryXML += "   <AND>\n";
        queryXML += "       <Predicate NodeType=\"" + nodetype + "\" Operand=\"EQ\"/>\n";
        if (notcondition.length() > 0) {
            queryXML += notcondition;
        }
        queryXML += "   <OR>\n";
        for (HashMap<String, String> map : list) {
            String path = map.get("path");
            String attrkey = map.get("key");
            String attrvalue = map.get("value");
            String attrtype = map.get("type");
            if (attrtype.equalsIgnoreCase("string")) {
                queryXML += " <Predicate Path=\"" + path + "\" AttributeName=\"" + attrkey
                        + "\" Operand=\"EQ\" ValueString=\""
                        + attrvalue
                        + "\" />\n";
            } else if (attrtype.equalsIgnoreCase("ref")) {
                queryXML += " <Predicate Path=\"" + path + "\" AttributeName=\"" + attrkey
                        + "\" Operand=\"EQ\" ValueRef=\""
                        + attrvalue
                        + "\" />\n";
            } else if (attrtype.equalsIgnoreCase("double")) {
                queryXML += " <Predicate Path=\"" + path + "\" AttributeName=\"" + attrkey
                        + "\" Operand=\"EQ\" ValueNumber=\""
                        + attrvalue
                        + "\" />\n";
            }
        }
        queryXML += "   </OR>\n";
        queryXML += "   </AND>\n";
        queryXML += order;
        queryXML += "</QUERY>\n";

        return queryXML;
    }

    public static Locale getLocaleFromLocaleName(String localeName) {
        Locale locale = null;
        if (!StringUtils.isEmpty(localeName)) {
            locale = Locale.forLanguageTag(localeName.replace('_', '-'));
        }
        return locale;
    }

    public static String stripBadChar(String s) {
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

    public static String writeFileToDisk(byte[] img, String filePath, String fileName) {
        String path = "";
        try {
            File folder = new File(filePath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            path = filePath + fileName;
            File file = new File(path);
            FileOutputStream fops = new FileOutputStream(file);
            fops.write(img);
            fops.flush();
            fops.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     *
     * @param json
     * @param classname
     * @param LOG
     * @return
     * @author GHUANG
     * @version 2019年6月10日 下午1:08:45
     */
//    public static HashMap parseXML(String type, JSONObject json, String classname) throws Exception{
//
//        String keys = CSProperties.getValue("cs." + type + ".import.keys", "");
//
//        List<String> keylist = Arrays.asList(keys.split(","));
//        HashMap objmap = new HashMap();
//        try {
//            for (String key : keylist) {
//                String xml = "";
//                String attrname = CSProperties.getValue("cs." + type + ".import." + key + ".attrname", "");
//                String attrtype = CSProperties.getValue("cs." + type + ".import." + key + ".attrtype", "");
//                String refobj = CSProperties.getValue("cs." + type + ".import." + key + ".refobject", "");
//                String refkey = CSProperties.getValue("cs." + type + ".import." + key + ".refkey", "");
//                String refpath = CSProperties.getValue("cs." + type + ".import." + key + ".refpath", "");
//                String attrpath = CSProperties.getValue("cs." + type + ".import." + key + ".attrpath", "");
//                if (json.isNull(key)) {
//                    continue;
//                }
//                if (json.has(key)) {
//                    String attrvalue = json.getString(key);
//                    if (attrtype.equals("enumKEY")) {
//                        xml += "<ChangeAttribute Id=\"" + attrname + "\" Type=\"enum\" Value=\""
//                                + refobj + attrvalue + "\" />";
//                        ;
//                    } else if (attrtype.equals("enumNAME")) {
//                        for (Entry<String, String> gmap : NodeUtil.zhLocaleMap.entrySet()) {
//                            if (gmap.getValue().equals(attrvalue) && gmap.getKey().contains(refobj)) {
//                                xml += "<ChangeAttribute Id=\"" + attrname + "\" Type=\"enum\" Value=\""
//                                        + gmap.getKey() + "\" />";
//
//                            }
//                        }
//                    } else if (attrtype.equals("time")) {
//                        String timestr = CommonUtil.parseTimestamp(attrvalue, refobj, classname);
//                        xml += "<ChangeAttribute Id=\"" + attrname + "\" Type=\"" + attrtype + "\" Value=\""
//                                + timestr + "\" />";
//                    } else if (attrtype.equals("integer")) {
//                        xml += "<ChangeAttribute Type=\"" + attrtype + "\" Id=\"" + attrname + "\"  Value=\""
//                                + Integer.valueOf(attrvalue)
//                                + "\" />";
//                    } else if (attrtype.equals("ref")) {
//                        String queryxml = "<Node Parameter=\"Type\" Op=\"EQ\" Value=\"" + refobj + "\" />\r\n" +
//                                "<Attribute Path=\"" + refpath + "\" Id=\"" + refkey + "\" Op=\"EQ\" SValue=\""
//                                + attrvalue + "\" />";
//                        List refobjlist = NodeUtil.queryBOByXML(queryxml);
//                        if (refobjlist.size() > 0) {
//                            xml += "<ChangeAttribute Id=\"" + attrname + "\" Type=\"" + attrtype + "\" Value=\""
//                                    + refobjlist.get(0) + "\" />";
//
//                        }
//                    } else if (attrtype.equals("reflist")) {
//                        List<String> valuelist = Arrays.asList(attrvalue);
//                        if (valuelist.size() > 0) {
//                            xml += "<ChangeAttribute Id=\"" + attrname + "\" Type=\"" + attrtype + "\" />";
//                            for (String value : valuelist) {
//                                String queryxml = "<Node Parameter=\"Type\" Op=\"EQ\" Value=\"" + refobj + "\" />\r\n" +
//                                        "<Attribute Path=\"" + refpath + "\" Id=\"" + value + "\" Op=\"EQ\" SValue=\""
//                                        + attrvalue + "\" />";
//                                List refobjlist = NodeUtil.queryBOByXML(queryxml);
//                                xml += "<ref>" + refobjlist.get(0) + "</ref>";
//                            }
//                            xml += "</ChangeAttribute>";
//                        }
//                    } else {
//                        xml += " <ChangeAttribute Id=\"" + attrname + "\" Type=\"" + attrtype
//                                + "\" ><![CDATA[" + attrvalue + "]]></ChangeAttribute>";
//
//                    }
//                }
//                if (attrpath == null || attrpath.length() == 0) {
//                    if (objmap.containsKey("default")) {
//                        String tempxml = (String) objmap.get("default");
//                        objmap.put("default", xml + tempxml);
//                    } else {
//                        objmap.put("default", xml);
//                    }
//                } else {
//                    System.out.println("---" + attrpath);
//                    if (objmap.containsKey(attrpath)) {
//                        String tempxml = (String) objmap.get(attrpath);
//                        objmap.put(attrpath, xml + tempxml);
//                    } else {
//                        objmap.put(attrpath, xml);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.info(classname, e);
//        }
//        return objmap;
//    }

    /**
     * 四舍五入
     *
     * @param attrvalue
     * @param dot
     * @return
     * @author GHUANG
     * @version 2019年6月18日 下午7:27:30
     */
    public static String changeDot(String attrvalue, int dot) {
        BigDecimal db = new BigDecimal(attrvalue);
        return String.valueOf(db.setScale(dot, BigDecimal.ROUND_HALF_UP).doubleValue());
    }

    public static void saveToFile(String fileName, InputStream in) throws IOException {
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        int BUFFER_SIZE = 1024;
        byte[] buf = new byte[BUFFER_SIZE];
        int size = 0;
        bis = new BufferedInputStream(in);
        File f = new File(fileName);
        if (!f.exists())//
        {
            File parentDir = new File(f.getParent());
            if (!parentDir.exists())//
            {
                parentDir.mkdirs();
            }
            f.createNewFile();
        }

        fos = new FileOutputStream(fileName);
        while ((size = bis.read(buf)) != -1) {
            fos.write(buf, 0, size);
        }
        fos.close();
        bis.close();
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

    public static String parseTime(String timeValue, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String resultValue = "";
        try {
            long timeLongValue = Long.parseLong(timeValue);
            if (timeLongValue > 0) {
                Timestamp ts = new Timestamp(timeLongValue);
                resultValue = sdf.format(ts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultValue;
    }

    public static String inputStream2String(InputStream is)
            throws UnsupportedEncodingException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
        StringBuffer sb = new StringBuffer();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /***
     * 删除文件夹
     *
     * @param folderPath 文件夹完整绝对路径
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            File myFilePath = new File(folderPath);
            boolean b = myFilePath.delete(); // 删除空文件夹
            if(!b){
                log.error("删除文件失败，路径错误！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 删除指定文件夹下所有文件
     *
     * @param path
     *            文件夹完整绝对路径
     * @return
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);// 再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

}
