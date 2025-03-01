package com.juege.RiskCtrlSys.flink.utils;

import com.juege.RiskCtrlSys.flink.redis.conf.JuegeRedisCommand;
import com.juege.RiskCtrlSys.flink.redis.source.JuegeRedisSource;
import com.juege.RiskCtrlSys.model.RedisPO;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * author: Juege
 * description: Flink读取Redis工具类
 * date: 2024
 */

public class RedisReadUtil {

    /**
     * author: Juege
     * description: Flink添加Source
     * @param env: Flink上下文环境
     * @param juegeRedisCommand: redis命令
     * @param key:  redis键
     * @return org.apache.flink.streaming.api.datastream.DataStream<com.juege.RiskCtrlSys.model.RedisPO>
     */
    public static DataStream<RedisPO> read(
            StreamExecutionEnvironment env,
            JuegeRedisCommand juegeRedisCommand,
            String key
            ) {

       return env.addSource(new JuegeRedisSource(juegeRedisCommand,key));
    }
}
