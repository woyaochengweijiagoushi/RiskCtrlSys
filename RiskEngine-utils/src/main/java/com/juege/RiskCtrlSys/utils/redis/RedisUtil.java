package com.juege.RiskCtrlSys.utils.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * author: Juege
 * description: Redis工具类
 * date: 2024
 */

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * author: Juege
     * description: RedisTemplate set
     * @param key:
     * @param value:
     * @return void
     */
    public void stringSet(String key,Object value) {
        redisTemplate.opsForValue().set(key,value);
    }

    /**
     * author: Juege
     * description: get
     * @param key:
     * @return java.lang.Object
     */
    public Object stringGet(String key) {
        Object res = redisTemplate.opsForValue().get(key);
        return res;
    }

    /**
     * author: Juege
     * description: RedisTemplate hash set
     * @param key:
     * @param map:
     * @return void
     */
    public void  hashSet(String key, Map<String,Object> map) {
        redisTemplate.opsForHash().putAll(key,map);
    }

    /**
     * author: Juege
     * description: RedisTemplate hash get
     * @param key:
     * @param hashKey:
     * @return java.lang.Object
     */
    public Object hashGet(String key,String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }
}
