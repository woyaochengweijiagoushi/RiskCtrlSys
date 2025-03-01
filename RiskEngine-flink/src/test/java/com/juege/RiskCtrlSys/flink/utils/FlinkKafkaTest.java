package com.juege.RiskCtrlSys.flink.utils;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

import java.util.Properties;

public class FlinkKafkaTest {

    public static void main(String[] args) throws Exception {
        // 创建 Flink 流式执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // Kafka 连接器配置
        Properties properties = new Properties();
//        properties.setProperty("fetch.timeout.ms", "300000");  // 设置请求超时时间为 30 秒
//        properties.setProperty("metadata.fetch.timeout.ms", "300000");
//        properties.setProperty("auto.offset.reset", "earliest");  // 确保从最早的消息开始消费
//        properties.setProperty("group.id", "imooc-testzzz");

        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers("165.154.187.67:9092")
                .setTopics("imooctest")
                .setGroupId("imooc-test")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setProperties(properties)  // 设置自定义配置
                .build();


        // 从 Kafka 获取数据流
        DataStream<String> stream = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "Kafka Source");

        // 打印数据到控制台
        SingleOutputStreamOperator<String> result = stream.map(new MapFunction<String, String>() {
            @Override
            public String map(String value) throws Exception {
                return "Received message: " + value;
            }
        });

        // 输出到控制台
        result.print();

        // 执行 Flink 作业
        env.execute("Flink Kafka Test Job");
    }
}
