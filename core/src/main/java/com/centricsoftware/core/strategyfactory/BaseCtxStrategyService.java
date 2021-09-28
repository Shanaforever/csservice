package com.centricsoftware.core.strategyfactory;


import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrSpliter;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.centricsoftware.commons.dto.ResEntity;
import com.centricsoftware.commons.utils.NodeUtil;
import com.centricsoftware.commons.utils.SpringUtil;
import com.centricsoftware.config.entity.CenterProperties;
import com.centricsoftware.config.entity.CsProperties;
import com.centricsoftware.pi.tools.xml.Element;
import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 接口开发实现类基类
 * @author ZhengGong
 * @date 2019/5/27
 */
public interface BaseCtxStrategyService{


    /**
     * service固定执行方法
     * @param params  Map<String,String>
     * @return ResEntity
     */
    ResEntity process(Map<String, String> params) throws Exception;

    /**
     * 根据配置文件，读取接口字段名称，并自动赋值
     *
     * @param configName configName
     * @param jsObject jsObject
     * @param url url
     * @param nodeMap nodeMap
     * @return jsObject
     */
    static JSONObject getJSONObjectFromProperties(String configName, JSONObject jsObject, String url,
                                                  HashMap<String, String> nodeMap) throws Exception{

        CsProperties properties = NodeUtil.getProperties();
        int count = Integer.parseInt(properties.getProperty(configName + ".count"));
        String paths =properties.getProperty(configName + ".path","");
        String attName;
        String attrType;
        String attValue;
        String attKey;
        String attrpath;
        ArrayListMultimap<String, Element> pathmap = ArrayListMultimap.create();
        Element element = NodeUtil.queryElementbyUri(url);

        if (paths != null && paths.length() > 0) {
            String[] path = paths.split(",");
            for (int i = 0; i < path.length; i++) {
                String refuri = NodeUtil.queryExpressionResult(path[i], url);
                List<String> splitUri = StrSpliter.split(refuri, ",", true, true);
                if (ObjectUtil.isNotEmpty(splitUri)) {
                    for (String uri : splitUri) {
                        Element pathElement = NodeUtil.queryElementbyUri(uri);
                        pathmap.put(StrUtil.trim(path[i]), pathElement);
                    }
                }
            }
        }
        for (int i = 1; i <= count; i++) {
            attName = properties.getProperty(configName + "." + i + ".name");
            attrType = properties.getProperty(configName + "." + i + ".type");
            attrpath = properties.getProperty(configName + "." + i + ".path");
            attValue = properties.getProperty(configName + "." + i + ".value","");
            attKey = properties.getProperty(configName + "." + i + ".key","");
            if (attValue == null)
                attValue = "";
            if ("".equals(attName)) {
                break;
            }
            if (attrpath != null && !attrpath.equals("")) {
                if (!attValue.startsWith("[")) {
                    List<Element> pElement = pathmap.get(StrUtil.trim(attrpath));

                    ArrayList<String> objects = Lists.newArrayList();
                    for (Element e : pElement) {
                        if(ObjectUtil.isEmpty(e)){
                            objects.add("");
                            continue;
                        }
                        String value = (String)NodeUtil.querySingleAttribute(e, attValue,false);
                        objects.add(value);
                    }
                    attValue = Joiner.on(",").join(objects);

                } else {
                    attValue = attValue.substring(1, attValue.length() - 1);
                    attValue = NodeUtil.queryExpressionResult(attValue, url);
                }
            }else {
                if (attValue.startsWith("[") && attValue.endsWith("]")) {
                    attValue = attValue.substring(1, attValue.length() - 1);
                    attValue = NodeUtil.queryExpressionResult(attValue, url);
                } else {
                    if(nodeMap!=null&&nodeMap.containsKey(attValue))
                        attValue = nodeMap.get(attValue);
                    else
                        attValue = (String)NodeUtil.querySingleAttribute(element, attValue,false);
                }
            }
            if (attrType != null && !"".equals(attrType)) {
                switch (attrType) {
                    case "double":
                        if ("".equals(attValue))
                            attValue = "0.00";
                        jsObject.putOnce(attName, Double.parseDouble(attValue));
                        break;
                    case "integer":
                        if ("".equals(attValue))
                            attValue = "0";
                        jsObject.putOnce(attName, NumberUtil.parseInt(attValue));
                        break;
                    case "boolean":
                        jsObject.putOnce(attName, Boolean.parseBoolean(attValue));
                        break;
                    case "enumName":
                        if (attValue.length() > 0) {
                            if (attValue.contains(":")) {
                                String key = attValue.substring(attValue.lastIndexOf(":") + 1);
                                if (key.length() > 0) {
                                    attValue = NodeUtil.getEnumEnDisplay(attValue);
                                    if (attValue == null || attValue.length() == 0) {
                                        attValue = key;
                                    }
                                } else {
                                    attValue = "";
                                }
                            } else {
                                attValue = NodeUtil.getEnumEnDisplay(attKey + ":" + attValue);
                            }

                        }
                        jsObject.putOnce(attName, attKey);
                        break;
                    case "enumDesc":
                        if (attValue.length() > 0) {
                            if (attValue.contains(":")) {
                                String key = attValue.substring(attValue.lastIndexOf(":") + 1);
                                String attr = attValue.substring(0, attValue.lastIndexOf(":"));
                                if (key.length() > 0) {
                                    attValue = (String) NodeUtil.queryEnumDescValue(attKey, attValue);
                                    if (attValue == null || attValue.length() == 0) {
                                        attValue = key;
                                    }
                                } else {
                                    attValue = "";
                                }
                            } else {
                                attValue = (String) NodeUtil.queryEnumDescValue(attKey, attValue);
                                if (attValue == null || attValue.length() == 0) {
                                    attValue = "";
                                }
                            }
                        }
                        jsObject.putOnce(attName, attValue);
                        break;
                    default:
                        jsObject.putOnce(attName, attValue);
                        break;
                }
            } else {
                jsObject.putOnce(attName, attValue);
            }
        }
        return jsObject;
    }

    /**
     * 将json转换excel坐标系
     * @param configName configName
     * @param jsObject  jsObject
     * @return List<Map<String, Object>>
     */
    static List<Map<String, Object>> getListFromJSON(String configName,JSONObject jsObject) {
        CsProperties properties = NodeUtil.getProperties();
        int count = Integer.parseInt(properties.getProperty(configName + ".count"));
        String x;
        String y;
        String value;
        ArrayList<Map<String, Object>> list = Lists.newArrayList();
        for (int i = 1; i <= count; i++) {
            x = properties.getProperty(configName + "[" + i + "].x");
            y = properties.getProperty(configName + "[" + i + "].y");
            value = properties.getProperty(configName + "[" + i + "].value");
            if(StrUtil.isBlank(x) || StrUtil.isBlank(y) || StrUtil.isBlank(value)){
                continue;
            }
            value= (String)jsObject.get(value);
            list.add(MapUtil.builder(new HashMap<String,Object>()).put("x",Integer.parseInt(x)).put("y",Integer.parseInt(y)).put("value",value).build());
        }
        return list;
    }

    /**
     * 根据配置文件，获取对应的属性和属性值，并且拼接为xml的报文
     * @param configName
     * @param url
     * @param nodeMap
     * @return
     */
    static String getXMLObjectFromProperties(String configName, String url,
                                                    HashMap<String, String> nodeMap) throws Exception{
        CsProperties properties = NodeUtil.getProperties();
        String resultStr = "";
        for (int i = 1; i < 300; i++) {
            String attName = properties.getValue(configName + "." + i + ".name");
            String attValue = properties.getValue(configName + "." + i + ".value");
            if (attValue == null)
                attValue = "";
            if ("".equals(attName))
                break;

            if (attValue.startsWith("[") && attValue.endsWith("]")) {
                attValue = attValue.substring(1, attValue.length() - 1);
                attValue = NodeUtil.queryExpressionResult(attValue, url);
            } else {
                attValue = nodeMap.get(attValue);
            }
            if (attValue == null)
                attValue = "";
            resultStr += "<"+attName+">" + attValue + "</"+attName+">\n\r";
        }

        return resultStr;
    }

}
