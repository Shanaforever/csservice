package com.centricsoftware.commons.utils;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * JSON工具类
 * 同类方法{@link cn.hutool.json.JSONUtil}
 * @author GHUANG
 * @version 2016年3月28日 下午8:15:11
 *
 *
 */
public class JsonHelper {
    /**
     *
     * @param javaBean
     * @return
     * @author GHUANG
     * @version 2016年3月28日 下午8:15:27
     */
    public static Map toMap(Object javaBean) {

        Map result = new HashMap();
        Method[] methods = javaBean.getClass().getDeclaredMethods();

        for (Method method : methods) {

            try {

                if (method.getName().startsWith("get")) {

                    String field = method.getName();
                    field = field.substring(field.indexOf("get") + 3);
                    field = field.toLowerCase().charAt(0) + field.substring(1);

                    Object value = method.invoke(javaBean, (Object[]) null);
                    result.put(field, null == value ? "" : value.toString());

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return result;

    }

    public static JSONArray getJAfromList(List<HashMap> list) throws Exception {
        JSONArray ja = new JSONArray();
        for (HashMap<String, String> map : list) {
            JSONObject json = new JSONObject();
            for (Entry<String, String> gmap : map.entrySet()) {
                String key = gmap.getKey();
                String value = gmap.getValue();
                json.put(key, value);
            }
            ja.put(json);
        }
        return ja;
    }

    public static JSONObject convertJS(JSONObject js) throws Exception {
        JSONObject json = new JSONObject();
        Iterator it = js.keys();
        while (it.hasNext()) {
            String key = (String) it.next();
            String value = js.getString(key);
            json.put(value, key);
        }
        return json;
    }

    public static JSONObject getJsonfromMap(HashMap<String, String> map) throws Exception {
        JSONObject json = new JSONObject();
        for (Entry<String, String> gmap : map.entrySet()) {
            String key = gmap.getKey();
            String value = gmap.getValue();
            json.put(key, value);

        }
        return json;
    }

    /**
     *
     * @param json
     * @return
     * @author GHUANG
     * @version 2019年5月15日 上午11:13:46
     */
    public static String getJsonToXml(JSONObject json) {
        Iterator<String> it = json.keys();
        StringBuffer sb = new StringBuffer();
        while (it.hasNext()) {
            String key = it.next().toString();
            String value = json.optString(key);
            try {
                if (value.startsWith("[")) {
                    JSONArray ja = new JSONArray(value);

                    sb.append("<").append(key).append(">");
                    if (ja.length() > 0) {
                        for (int j = 0; j < ja.length(); j++) {
                            JSONObject sjs = ja.getJSONObject(j);
                            if (key.equals("JSON")) { // 去掉非
                                sb.append("<").append(key).append(">");
                                String sbstr = getJsonToXml(sjs);
                                sb.append(sbstr);
                                sb.append("</").append(key).append(">");
                            } else {
                                String sbstr = getJsonToXml(sjs);
                                sb.append(sbstr);
                            }
                        }
                    }
                    sb.append("</").append(key).append(">");

                } else if (value.startsWith("{")) {
                    JSONObject jsonSon = new JSONObject(value);
                    sb.append("<").append(key).append(">");
                    sb.append(getJsonToXml(jsonSon));
                    sb.append("</").append(key).append(">");
                } else {
                    sb.append("<").append(key).append(">").append(value).append("</").append(key)
                            .append(">");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     *
     * @param jsonString
     * @return
     * @throws JSONException
     * @author GHUANG
     * @version 2016年3月28日 下午8:15:44
     */
    public static HashMap<String, String> toMap(String jsonString) throws JSONException {

        JSONObject jsonObject = new JSONObject(jsonString);

        HashMap<String, String> result = new HashMap<String, String>();
        Iterator iterator = jsonObject.keys();
        String key = null;
        String value = null;

        while (iterator.hasNext()) {

            key = (String) iterator.next();
            value = jsonObject.getString(key);
            result.put(key, value);

        }
        return result;

    }

    /**
     *
     * @param jsonString
     * @return
     * @throws JSONException
     * @author GHUANG
     * @version 2016年3月28日 下午8:15:44
     */
    public static ArrayList toMapfromArray(String jsonString) throws JSONException {

        JSONArray jsonObject = new JSONArray(jsonString);
        ArrayList infoList = new ArrayList();
        for (int i = 0; i < jsonObject.length(); i++) {
            String jobj = jsonObject.getJSONObject(i).toString();
            HashMap<String, String> map = JsonHelper.toMap(jobj);
            // System.out.println("map="+map.toString());
            infoList.add(map);
        }

        return infoList;

    }

    /**
     *
     * @param bean
     * @return
     * @author GHUANG
     * @version 2016年3月28日 下午8:15:51
     */
    public static JSONObject toJSON(Object bean) {

        return new JSONObject(toMap(bean));

    }

    /**
     *
     * @param javabean
     * @param data
     * @return
     * @author GHUANG
     * @version 2016年3月28日 下午8:15:57
     */
    public static Object toJavaBean(Object javabean, Map data) {

        Method[] methods = javabean.getClass().getDeclaredMethods();
        for (Method method : methods) {

            try {
                if (method.getName().startsWith("set")) {

                    String field = method.getName();
                    field = field.substring(field.indexOf("set") + 3);
                    field = field.toLowerCase().charAt(0) + field.substring(1);
                    method.invoke(javabean, new Object[] {

                            data.get(field)

                    });

                }
            } catch (Exception e) {
            }

        }

        return javabean;

    }

    /**
     * 通过JSONObject获取对应的key值(String)
     *
     * @param jsonObj
     * @param key
     * @return
     */
    private static String getJSONString(JSONObject jsonObj, String key) {
        String retString = "";
        try {
            Object obj = jsonObj.get(key);
            if (obj != null) {
                retString = String.valueOf(obj);
            }
        } catch (Exception e) {
            // TODO: handle exception
            retString = "";
        }
        return retString;
    }

    /**
     *
     * @param javabean
     * @param jsonString
     * @throws ParseException
     * @throws JSONException
     * @author GHUANG
     * @version 2016年3月28日 下午8:16:04
     */
    public static void toJavaBean(Object javabean, String jsonString)
            throws ParseException, JSONException {

        JSONObject jsonObject = new JSONObject(jsonString);

        Map map = toMap(jsonObject.toString());

        toJavaBean(javabean, map);

    }

    public static HashMap getJsonToMap(JSONObject json) throws Exception {
        HashMap map = new HashMap();
        for (Iterator<?> it = json.keys(); it.hasNext();) {
            String key = (String) it.next();
            String value = json.getString(key);
            map.put(key, value);

        }
        return map;
    }

    public static HashMap getMapfromJson(JSONObject json) throws Exception {
        HashMap map = new HashMap();
        for (Iterator<?> it = json.keys(); it.hasNext();) {
            String key = (String) it.next();
            String value = json.getString(key);
            map.put(key, value);

        }
        return map;
    }

    public static ArrayList getListfromJa(JSONArray ja) throws Exception {
        ArrayList list = new ArrayList();
        for (int i = 0; i < ja.length(); i++) {
            JSONObject json = ja.getJSONObject(i);
            HashMap<String, Object> map = getMapfromJson(json);
            list.add(map);
        }
        return list;
    }

    public static JSONObject parseJS(JSONObject a, JSONObject b) throws Exception {
        Iterator it = a.keys();
        while (it.hasNext()) {
            String key = (String) it.next();
            Object value = a.get(key);
            if (value instanceof String) {
                b.put(key, value);
            } else if (value instanceof JSONArray) {
                JSONArray ja = (JSONArray) value;
                b.put(key, ja);
            }

        }
        return b;
    }

    public static Date[] getJsonToDateArray(String jsonString) throws ParseException, JSONException {

        JSONArray jsonArray = new JSONArray(jsonString);
        Date[] dateArray = new Date[jsonArray.length()];
        String dateString;
        Date date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < jsonArray.length(); i++) {
            dateString = jsonArray.getString(i);
            try {
                date = sdf.parse(dateString);
                dateArray[i] = date;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dateArray;
    }

}