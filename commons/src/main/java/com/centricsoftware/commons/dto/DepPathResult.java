package com.centricsoftware.commons.dto;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.*;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 非线程安全
 */
@Slf4j
public class DepPathResult {
    private static final char[] expChars = { CharUtil.DOT};
    @Getter
    private JSONArray nodes;// 结果集
    private HashMap<String,JSONObject> urlNodes = new HashMap(128); //结果集对应的map
    private HashMap<String,JSONObject> pathNodes = new HashMap(128);//path集对应的map
    @Getter
    private HashMap<String,JSONObject> allNodes = new HashMap(256);//结果集+path集对应的map
    private boolean isStartWith$ = false;

    private DepPath depPath ;

    private ThreadLocal<Object> result = new ThreadLocal<>();
    protected ThreadLocal<List<String>> patternParts = new ThreadLocal<>();//表达式拆分

    @Setter
    private JSONObject completeResultRefs;

    public int size(){
        return nodes.size();
    }

    /**
     * XML查询调用方法 返回string
     * @param i
     * @param expression
     * @return
     */
    public String getStrXML(int i,String expression){
        JSONObject obj = JSONUtil.parseObj(nodes.get(i));
        return getStr(expression,obj.getStr("$URL"));
    }
    /**
     * XML查询调用方法 返回list
     * @param i
     * @param expression
     * @return
     */
    public  <T> List<T> getListXML(int i,String expression){
        JSONObject obj = JSONUtil.parseObj(nodes.get(i));
        return getList(expression,obj.getStr("$URL"));
    }
    /**
     * XML查询调用方法 返回map
     * @param i
     * @param expression
     * @return
     */
    public Map<String, String> getMapXML(int i,String expression){
        JSONObject obj = JSONUtil.parseObj(nodes.get(i));
        return getMap(expression,obj.getStr("$URL"));
    }

    public static DepPathResult init(JSONArray urlArray,JSONArray pathArray,DepPath depPath){
        return DepPathResult.init(urlArray,pathArray,depPath,null);
    }

    public static DepPathResult init(JSONArray urlArray,JSONArray pathArray,DepPath depPath,JSONObject completeResultRefs){
        DepPathResult result = new DepPathResult();
        result.setCompleteResultRefs(completeResultRefs);
        if(urlArray!=null){
            result.nodes = urlArray;
            urlArray.forEach(i->{
                JSONObject obj = JSONUtil.parseObj(i);
                result.urlNodes.put(obj.getStr("$URL"),obj);
            });
            result.allNodes.putAll( result.urlNodes);
        }
        if(pathArray!=null){
            result.depPath = depPath;
            pathArray.forEach(i->{
                JSONObject obj = JSONUtil.parseObj(i);
                result.pathNodes.put(obj.getStr("$URL"),obj);
            });
            result.allNodes.putAll( result.pathNodes);
        }
        return result;
    }

    public long getLong(String expression,String url){
        String str =  getStr( expression, url);
        return Long.parseLong(str);
    }

    public String getStr(String expression,String url){
        getObject(expression,url);
        String re = Convert.toStr(result.get());
        if("centric:".equals(re)||"null".equals(re)|| StrUtil.isEmpty(re)){
            return "";
        }
        return re;
    }

    public Map<String, String> getMap(String expression, String url){
        Map<String, String> map = getMap(String.class, String.class, expression, url);
        return map;
    }

    public <K, V> Map<K, V> getMap(Class<K> key,Class<V> value,String expression,String url){
        getObject(expression,url);
        Map<K, V> map = null;
        try {
            JSONObject jsonObject = JSONUtil.parseObj(result.get());
            map = (Map<K, V>) Convert.toMap(key, value, jsonObject);
        } catch (Exception e) {
            HashMap<K, V> kvHashMap = MapUtil.newHashMap();
            return kvHashMap;
        }
        return map;
    }


    public <T> List<T> getList(String expression,String url){
        return (List<T>) getList(expression,url,true,String.class);
    }

    public <T> List<T> getList(String expression,String url,Class<T> elementType){
        return getList(expression,url,true,elementType);
    }

    public <T> List<T> getList(String expression,String url,boolean removeDuplicate,Class<T> elementType){
        List<T> ts = (List<T>) Lists.newArrayList();
        getObject(expression,url);
        Object result = this.result.get();
        try {
            if(StrUtil.startWith(result.toString(),'[')&&StrUtil.endWith(result.toString(),']')){
                ts = JSONUtil.parseArray(result).toList(elementType);
            }else{
                if(StrUtil.isNotBlank(result.toString())){
                    ts.add((T) result);
                }
            }
            if(removeDuplicate){
                return ts.stream() .distinct().collect(Collectors.toList());
            }else{
                return ts;
            }
        } catch (Exception e) {
            log.error(result+"",e);
            return ts;
        }
    }


    public Object getObject(String expression,String url){
        Object result = null;
        try {
            analysisExpression(expression);//解析表达式
            if(patternParts.get().size()==0){
                //表达式为空，则直接返回null
                return null;
            }
            if(!allNodes.containsKey(url)){
                //url填写不正确，返回空
                return null;
            }

            result = ObjectUtil.cloneByStream(allNodes.get(url));//获取url对应的node
            this.result.set(result);
            String first = patternParts.get().get(0);
            if(first.endsWith("]")){
                getIndexPattern(url,0);
            }else{
                //第一个表达式为普通表达式
                JSONObject jsonObject = ObjectUtil.cloneByStream(allNodes.get(url));
                result = jsonObject.get(first);
                this.result.set(result);
            }
            if(patternParts.get().size()>1){
                getValue(1);//开始解析第二个表达式和之后的值
            }

        } catch (Exception e) {
            log.error("DepPath获取值失败:expression={},url={}",expression,url);
            log.error("DepPath获取值失败:",e);
            this.result.set(null);
        }
        return this.result.get();
    }

    /**
     *
     * @param i  i从1开始计算
     */
    private void getValue(int i){
        Object result = this.result.get();
        if(result==null){
            return;
        }
        boolean isContinue = true;
        if(StrUtil.startWith(result.toString(),'[')&&StrUtil.endWith(result.toString(),']')){
            if(toList(i)){
                isContinue = false;
            }
        }
        if(isContinue&&StrUtil.startWith(result.toString(),'{')&&StrUtil.endWith(result.toString(),'}')){
            if(toMap(i)){
                isContinue = false;
            }
        }
        if(isContinue){
            toStr(i);
        }
        if(i==patternParts.get().size()-1){
            return;
        }
        i++;
        getValue(i);
    }

    /**
     * 上一次解析结果为map
     * @param i
     * @return
     */
    private boolean toMap(int i){
        Object result = this.result.get();
        Map<String,Object> m = Maps.newHashMap();
        try {
            Map<String,Object> map  =Convert.toMap(String.class,Object.class,JSONUtil.parseObj(result));
            for(String key:map.keySet()){
                String value = map.get(key)==null?"":map.get(key).toString();
                JSONObject obj = ObjectUtil.cloneByStream(allNodes.get(value)) ;
                String pattern = patternParts.get().get(i);
                String k= pattern;
                if(pattern.endsWith("]")){
                    //按照角标的key返回数据
                    k = pattern.split("\\[")[0];
                    String index = pattern.split("\\[")[1].replace("]","");//下标
                    if(obj.containsKey(k)){
                        if(StrUtil.isBlank(index)&&StrUtil.isBlank(key)){
                            m.put(key,obj.getStr(k));
                        }else if(!StrUtil.isBlank(key)&&key.equals(index)){
                            m.put(key,obj.getStr(k));
                        }
                        return true;
                    }
                }else{
                    if(obj.containsKey(pattern)){
                        String str = obj.getStr(pattern);
                        if(str.endsWith("}")){
                            //数据为map
                            m.putAll(Convert.toMap(String.class,Object.class,JSONUtil.parseObj(str)));
                        }else{
                            m.put(key,obj.getStr(pattern));
                        }
                    }
                }
            }
            if(m.isEmpty()){
                result = null;
            }else{
                map.putAll(m);
            }
            this.result.set(map);
        } catch (ConvertException e) {
            return false;
        }
        return true;
    }

    /**
     * 上一次解析结果为String
     * @param i
     * @return
     */
    private boolean toStr(int i){
        try {
            String s  = Convert.toStr(result.get());
            if(allNodes.containsKey(s)){

                JSONObject obj = ObjectUtil.cloneByStream(allNodes.get(s)) ;
                String key = patternParts.get().get(i);
                if(obj.containsKey(key)){
                    result.set(obj.getStr(key));
                    return true;
                }else if(StrUtil.endWith(key,']')){
                    getIndexPattern(s,i);
                    return true;
                }
            }
            result.set(null);
        } catch (ConvertException e) {
            return false;
        }
        return true;
    }

    /**
     * 上一次解析结果为List
     * @param i
     * @return
     */
    private boolean toList(int i){
        try {
            List<String> list  = Convert.toList(String.class,result.get());
            List<String>  re = Lists.newArrayList();
            for( Integer j=0;j<list.size();j++){
                String s = list.get(j);
                if(allNodes.containsKey(s)){
                    JSONObject obj = ObjectUtil.cloneByStream(allNodes.get(s)) ;
                    String pattern = patternParts.get().get(i);
                    String k = pattern;
                    if(pattern.endsWith("]")) {
                        //获取角标数据
                        return getListIndex(j,pattern,obj);
                    }else{
                        if(obj.containsKey(pattern)){
                            String str = obj.getStr(pattern);
                            if(str.endsWith("]")) {
                                //str是个数组
//                                re.addAll(Convert.toList(String.class,str));
                                re.addAll(JSONUtil.parseArray(str).toList(String.class));
                            }else{
                                re.add(str);
                            }
                        }
                    }
                }
            }
            if(re.size()==0){
                result.set(null);
            }else{
                result.set(re);
            }
        } catch (ConvertException e) {
            return false;
        }
        return true;
    }

    /**
     * 表达式分析
     * @param expression
     */
    private void analysisExpression(String expression) {
        List<String> localPatternParts = new ArrayList<>();
        int length = expression.length();

        final StrBuilder builder = StrUtil.strBuilder();
        char c;
        boolean isNumStart = false;// 下标标识符开始
        for (int i = 0; i < length; i++) {
            c = expression.charAt(i);
//            if (0 == i && '$' == c) {
//                // 忽略开头的$符，表示当前对象
//                isStartWith$ = true;
//                continue;
//            }
            if (ArrayUtil.contains(expChars, c)) {
                // 处理边界符号
                if (CharUtil.BRACKET_END == c) {
                    // 中括号（数字下标）结束
                    if (false == isNumStart) {
                        throw new IllegalArgumentException(StrUtil.format("Bad expression '{}':{}, we find ']' but no '[' !", expression, i));
                    }
                    isNumStart = false;
                    // 中括号结束加入下标
                } else {
                    if (isNumStart) {
                        // 非结束中括号情况下发现起始中括号报错（中括号未关闭）
                        throw new IllegalArgumentException(StrUtil.format("Bad expression '{}':{}, we find '[' but no ']' !", expression, i));
                    } else if (CharUtil.BRACKET_START == c) {
                        // 数字下标开始
                        isNumStart = true;
                    }
                    // 每一个边界符之前的表达式是一个完整的KEY，开始处理KEY
                }
                if (builder.length() > 0) {
                    localPatternParts.add(unWrapIfPossible(builder));
                }
                builder.reset();
            } else {
                // 非边界符号，追加字符
                builder.append(c);
            }
        }
        if(!builder.isEmpty()){
            localPatternParts.add(builder.toString());
        }
        patternParts.set(localPatternParts);
    }

    /**
     * 对于非表达式去除单引号
     *
     * @param expression 表达式
     * @return 表达式
     */
    private static String unWrapIfPossible(CharSequence expression) {
        if (StrUtil.containsAny(expression, " = ", " > ", " < ", " like ", ",")) {
            return expression.toString();
        }
        return StrUtil.unWrap(expression, '\'');
    }

    /**
     * 将角标数据放入result
     * @param pattern
     * @param obj
     * @return
     */
    private boolean getListIndex(Integer i,String pattern,JSONObject obj){
        //针对下标取值
        String k = pattern.split("\\[")[0];
        String index = pattern.split("\\[")[1].replace("]","");//下标
        if(i.toString().equals(index)){
            if(obj.containsKey(k)){
                result.set(obj.getStr(pattern));
                return true;//返回
            }
        }
        return true;
    }

    /**
     * 处理第num个值
     * @return
     */
    private boolean getIndexPattern(String url,int num){
        String first = patternParts.get().get(num);
        String index = first.split("\\[")[1].replace("]","");//下标
        String key = first.split("\\[")[0];//key 字段属性
        if(NumberUtil.isNumber(index)){
            //数组取值
            JSONObject jsonObject = ObjectUtil.cloneByStream(allNodes.get(url));
            List<String> strings = Convert.toList(String.class, jsonObject.get(key));
            int i = Convert.toInt(index);
            if(i<strings.size()){
                result.set(strings.get(Convert.toInt(index)));
                return true;
            }else {
                //数组越界
                result.set(null);
                return false;
            }
        }else{
            //Map 取值
            String s1 = allNodes.get(url).getStr(key);
            Map<String, Object> stringObjectMap = Convert.toMap(String.class, Object.class,JSONUtil.parseObj(s1) );
            if(stringObjectMap.containsKey(index)){
                result.set(stringObjectMap.get(index));
                return true;
            }else{
                result.set(null);
                return false;
            }
        }
    }

    /**
     * 获取search返回值
     * @return
     */
    public List<String> getResultRefs(){
        if(completeResultRefs==null){
            return Lists.newArrayList();
        }
        return completeResultRefs.get("ref",List.class);
    }

}
