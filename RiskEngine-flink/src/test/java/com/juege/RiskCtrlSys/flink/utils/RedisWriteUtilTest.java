package com.juege.RiskCtrlSys.flink.utils;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

class RedisWriteUtilTest {

    @DisplayName("测试基于Bahir写入Redis,Redis数据类型是String类型")
    @Test
    void testWriteByBahirWithString() throws Exception {
        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();

        Tuple2<String,String> tuple = Tuple2.of("juege:bahir", "this is write by bahir with String");
        DataStream<Tuple2<String,String>> dataStream = env.fromElements(tuple);

        RedisWriteUtil.writeByBahirWithString(dataStream);

        env.execute();
    }

    @Test
    void testWriteDemo() throws Exception {
        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort("165.154.187.67", 8802));

        int timeout = 3000; // 超时时间
        int maxAttempts = 10; // 最大重试次数

        JedisCluster jedisCluster = new JedisCluster(nodes, timeout, timeout, maxAttempts, "tlb888888", new GenericObjectPoolConfig<>());
        jedisCluster.set("a","hello redis");
        System.out.println(jedisCluster.get("a"));
    }


}
