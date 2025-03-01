package com.juege.RiskCtrlSys.flink.utils;

import com.jayway.jsonpath.MapFunction;
import com.juege.RiskCtrlSys.flink.redis.conf.JuegeRedisCommand;
import com.juege.RiskCtrlSys.model.RedisPO;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * author: juege
 * description: redis读取工具类单元测试
 * date:  2024
*/
class RedisReadUtilTest {

    @DisplayName("测试自定义Source读取Redis,Redis数据类型是String类型")
    @Test
    void testReadByCustomSourceWithString() throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(20);

        DataStream<RedisPO> dataStream = RedisReadUtil.read(
                env,
                JuegeRedisCommand.GET,
                "testKey"
        );

        dataStream
                .filter(new MyFilterFunction())
                .map(new MyRichMapFunction())
                .addSink(new MyRedisSinkFunction( JuegeRedisCommand.GET,
                "testKey"));
        env.execute();
    }
}
