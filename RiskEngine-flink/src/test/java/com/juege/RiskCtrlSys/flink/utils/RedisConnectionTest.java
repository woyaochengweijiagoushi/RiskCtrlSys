package com.juege.RiskCtrlSys.flink.utils;

import redis.clients.jedis.Jedis;

public class RedisConnectionTest {
    public static void main(String[] args) {
        // 配置 Redis 的 IP 地址和端口
        String redisHost = "165.154.187.67"; // 根据你的实际 IP 地址
        int redisPort = 8802; // Redis 容器暴露的端口
        String redisPassword = "tlb888888"; // Redis 密码

        // 创建 Jedis 实例
        try (Jedis jedis = new Jedis(redisHost, redisPort,50000000)) {
            // 设置密码
            jedis.auth(redisPassword);
            
            // 测试连接
            String response = jedis.ping();
            System.out.println("Connection successful, server response: " + response);

            // 测试读写操作
            jedis.set("testKey", "Hello Redis");
            String value = jedis.get("testKey");
            System.out.println("Retrieved value: " + value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
