package com.centricsoftware.redis.component;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
/**
 * redis api封装
 * @author zheng.gong
 * @date 2020/4/21
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RedisExecutor {

    private final StringRedisTemplate redisTemplate;

    private int validTime;
    /**
     * 查询key,支持模糊查询
     *
     * @param key 传过来时key的前后端已经加入了*，或者根据具体处理
     * */
    public Set<String> keys(String key){
        return redisTemplate.keys(key);
    }

    /**
     * 字符串获取值
     * @param key redis key
     * */
    public Object get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 字符串存入值
     * 默认过期时间为2小时
     * @param key redis key
     * */
    public void set(String key, String value){
        redisTemplate.opsForValue().set(key,value, 7200,TimeUnit.SECONDS);
    }

    /**
     * 字符串存入值
     * @param expire 过期时间（毫秒计）
     * @param key redis key
     * */
    public void set(String key, String value,Integer expire){
        redisTemplate.opsForValue().set(key,value, expire,TimeUnit.SECONDS);
    }

    /**
     * 删出key
     * 这里跟下边deleteKey（）最底层实现都是一样的，应该可以通用
     * @param key redis key
     * */
    public void delete(String key){
        redisTemplate.opsForValue().getOperations().delete(key);
    }

    /**
     * 添加单个
     * 默认过期时间为两小时
     * @param key    key
     * @param filed  filed
     * @param domain 对象
     */
    public void hset(String key,String filed,Object domain){
        redisTemplate.opsForHash().put(key, filed, domain);
    }

    /**
     * 添加单个
     * @param key    key
     * @param filed  filed
     * @param domain 对象
     * @param expire 过期时间（毫秒计）
     */
    public void hset(String key,String filed,Object domain,Integer expire){
        redisTemplate.opsForHash().put(key, filed, domain);
        redisTemplate.expire(key, expire,TimeUnit.SECONDS);
    }

    /**
     * 添加HashMap
     *
     * @param key    key
     * @param hm    要存入的hash表
     */
    public void hset(String key, HashMap<String,Object> hm){
        redisTemplate.opsForHash().putAll(key,hm);
    }

    /**
     * 如果key存在就不覆盖
     * 如果变量值存在，在变量中可以添加不存在的的键值对，如果变量不存在，则新增一个变量，同时将键值对添加到该变量
     * @param key redis key
     * @param filed new key
     * @param domain value
     */
    public void hsetAbsent(String key,String filed,Object domain){
        redisTemplate.opsForHash().putIfAbsent(key, filed, domain);
    }

    /**
     * 查询key和field所确定的值
     *
     * @param key 查询的key
     * @param field 查询的field
     * @return HV
     */
    public Object hget(String key,String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 查询该key下所有值
     *
     * @param key 查询的key
     * @return Map<HK, HV>
     */
    public Object hget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 删除key下所有值
     *
     * @param key 查询的key
     */
    public void deleteKey(String key) {
        redisTemplate.opsForHash().getOperations().delete(key);
    }

    /**
     * 判断key和field下是否有值
     *
     * @param key 判断的key
     * @param field 判断的field
     */
    public Boolean hasKey(String key,String field) {
        return redisTemplate.opsForHash().hasKey(key,field);
    }

    /**
     * 判断key下是否有值
     *
     * @param key 判断的key
     */
    public Boolean hasKey(String key) {
        return redisTemplate.opsForHash().getOperations().hasKey(key);
    }

    /**
     * String类型设值并设置过期
     * @param k 键
     * @param v 值
     * @param l 时间
     * @param t 类型
     */
    public void set(String k,String v,long l,TimeUnit t){
        redisTemplate.opsForValue().set(k,v,l,t);
    }

    /**
     * 获取键的过期时间
     * @param k 键
     * @param t 时间类型
     * @return Long
     */
    public Long getExpire(String k,TimeUnit t){
        return redisTemplate.opsForValue().getOperations().getExpire(k,t);
    }

    /**
     * 设置k,v当且仅当k不存在
     * @param k key
     * @param v value
     * @return Boolean
     */
    public Boolean setNx(String k,String v){
        return redisTemplate.execute((RedisCallback<Boolean>) redisConnection -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            return redisConnection.setNX(Objects.requireNonNull(serializer.serialize(k)), Objects.requireNonNull(serializer.serialize(v)));
        });
    }

}
