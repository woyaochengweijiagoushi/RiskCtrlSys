package com.juege.RiskCtrlSys.flink.utils;

import com.juege.RiskCtrlSys.flink.redis.conf.JedisBuilder;
import com.juege.RiskCtrlSys.flink.redis.conf.JedisConf;
import com.juege.RiskCtrlSys.flink.redis.conf.JuegeRedisCommand;
import com.juege.RiskCtrlSys.model.RedisPO;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import redis.clients.jedis.Jedis;

public class MyRedisSinkFunction extends RichSinkFunction<RedisPO> {

    /**
     * Jedis对象
     */
    private JedisBuilder jedisBuilder;

    /**
     * Redis命令枚举对象
     */
    private final JuegeRedisCommand juegeRedisCommand;

    /**
     * redis key
     */
    private final String key;

    public MyRedisSinkFunction(JuegeRedisCommand juegeRedisCommand, String key) {
        this.juegeRedisCommand = juegeRedisCommand;
        this.key = key;
    }

    /**
     * author: Juege
     * description: Redis的连接初始化
     * @param parameters:
     * @return void
     */
    @Override
    public void open(Configuration parameters) throws Exception {
//        JedisCluster jedisCluster = JedisConf.getJedisCluster();
//        jedisBuilder = new JedisBuilder(jedisCluster);
        Jedis jedis = JedisConf.getJedis();
        jedisBuilder = new JedisBuilder(jedis);

    }


    @Override
    public void invoke(RedisPO value, Context context) throws Exception {
        jedisBuilder.set(key,value.getData());
        System.out.println("change value of redis key:"+key+" to "+ value);
    }
}
