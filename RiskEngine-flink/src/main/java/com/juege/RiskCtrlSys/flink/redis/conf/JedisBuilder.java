package com.juege.RiskCtrlSys.flink.redis.conf;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

/**
 * author: Juege
 * description: 封装Jedis对象的redis方法
 * date: 2024
 */

public class JedisBuilder {

//    private JedisCluster jedisCluster = null;
    private Jedis jedis = null;


//    public JedisBuilder(JedisCluster jedisCluster) {
//        this.jedis = jedisCluster;
//    }
    public JedisBuilder(Jedis jedis) {
        this.jedis = jedis;
    }

    public void close() {
        if (this.jedis != null) {
            this.jedis.close();
        }
    }

    /**
     * author: Juege
     * description: Redis的Get方法
     * @param key:  redis key
     * @return java.lang.String
     */
    public String get(String key) {
        return jedis.get(key);
    }

    /**
     * author: Juege
     * description: Redis的Set方法
     * @param key:  redis key
     * @param value:  redis value
     * @return java.lang.String
     */
    public String set(String key,String value) {
        return jedis.set(key,value);
    }
}
