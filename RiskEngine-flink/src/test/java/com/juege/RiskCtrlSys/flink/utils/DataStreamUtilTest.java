package com.juege.RiskCtrlSys.flink.utils;

import com.juege.RiskCtrlSys.flink.job.keyby.MetricKeyBy;
import com.juege.RiskCtrlSys.flink.job.process.MetricKeyedProcessFunc;
import com.juege.RiskCtrlSys.flink.job.watermark.MetricTimestampAssigner;
import com.juege.RiskCtrlSys.model.EventPO;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;


/**
 * author: Juege
 * description: 数据流工具类单元测试
 * date: 2024
 */

public class DataStreamUtilTest {


    @DisplayName("测试生成流式计算上下文环境")
    @Test
    public void testStreamExecutionEnvironmentInit() throws Exception {

        //通过工具类初始化环境
        DataStreamUtil.initEnv();
        //获取初始化后的流式计算上下文环境
        StreamExecutionEnvironment env = DataStreamUtil.env;

        //打印检查点超时时间
        System.out.println(env.getCheckpointConfig().getCheckpointTimeout());
    }

    @DisplayName("测试打印出自动注入WaterMark的事件流")
    @Test
    void testWaterMarkByAuto() throws Exception {

        ParameterTool parameterTool = ParameterUtil.getParameters();
        StreamExecutionEnvironment env = KafkaUtil.env;
        env.setParallelism(3);

        DataStream<EventPO> eventStream = KafkaUtil.read(parameterTool);

        long maxOutOfOrderness =
                parameterTool.getInt(ParameterConstantsUtil.FLINK_MAXOUTOFORDERNESS) * 1000L;


        eventStream
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                //水印生成器: 实现一个延迟10秒的固定延迟水印
                                .<EventPO>forBoundedOutOfOrderness(Duration.ofMillis(maxOutOfOrderness))
                                //时间戳生成器：提取事件流的event_time字段
                                .withTimestampAssigner(new MetricTimestampAssigner())
                )
                .keyBy(new MetricKeyBy())
                .process(new MetricKeyedProcessFunc())
                .print();
        env.execute();
    }
}
