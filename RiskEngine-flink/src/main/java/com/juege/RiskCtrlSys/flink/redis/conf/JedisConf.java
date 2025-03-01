package com.juege.RiskCtrlSys.flink.redis.conf;

import com.juege.RiskCtrlSys.flink.utils.ParameterUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * author: Juege
 * description: Jedis配置类~
 * date: 2024
 */

public class JedisConf {

    public static JedisCluster getJedisCluster() throws IOException {

        ParameterTool parameterTool =
                ParameterUtil.getParameters();
        String host = parameterTool.get("redis.host");
        String port = parameterTool.get("redis.port");
        String password = parameterTool.get("redis.pass");

        /* **********************
         *
         * 知识点：
         *
         * Jedis对象
         *
         * JedisPool : 用于redis单机版
         * JedisCluster: 用于redis集群
         *
         * JedisCluster对象能够自动发现正常的redis结节
         *
         * *********************/
        Set<HostAndPort> nodes = new HashSet<>();

        HostAndPort hostAndPort = new HostAndPort(
                host,
                Integer.parseInt(port)
        );
        nodes.add(hostAndPort);

//        nodes.add(new HostAndPort("165.154.187.67",Integer.parseInt("7000")));
//        nodes.add(new HostAndPort("165.154.187.67",Integer.parseInt("7001")));
//        nodes.add(new HostAndPort("165.154.187.67",Integer.parseInt("7002")));
//        nodes.add(new HostAndPort("165.154.187.67",Integer.parseInt("7003")));
//        nodes.add(new HostAndPort("165.154.187.67",Integer.parseInt("7004")));
//        nodes.add(new HostAndPort("165.154.187.67",Integer.parseInt("7005")));


//        return new JedisCluster(nodes);
        return new JedisCluster(nodes, 2000, 2000, 5, password, null);

    }

    public static Jedis getJedis() throws IOException {
        // 配置 Redis 的 IP 地址和端口
        ParameterTool parameterTool =
                ParameterUtil.getParameters();
        String redisHost = parameterTool.get("redis.host");
        String redisPort = parameterTool.get("redis.port");
        String redisPassword = parameterTool.get("redis.pass");

        // 创建 Jedis 实例
        try (
                Jedis jedis = new Jedis(redisHost, Integer.parseInt(redisPort),5000000)) {
            // 设置密码
            jedis.auth(redisPassword);
            return jedis;

//        // 测试连接
//        String response = jedis.ping();
//        System.out.println("Connection successful, server response: " + response);
//
//        // 测试读写操作
//        jedis.set("testKey", "Hello Redis");
//        String value = jedis.get("testKey");
//        System.out.println("Retrieved value: " + value);
        }
    }
}
