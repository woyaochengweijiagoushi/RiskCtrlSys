package com.juege.RiskCtrlSys.flink.utils;

import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.kafka.common.TopicPartition;
import org.apache.flink.api.common.serialization.SimpleStringSchema;

import java.util.HashMap;
import java.util.Map;

public class KafkaSourceExample {
    public static void main(String[] args) {
        String topic = "my-topic";
        int partition = 4;
        long offset = 500L;

        // 创建 TopicPartition
        TopicPartition topicPartition = new TopicPartition(topic, partition);

        // 构造特定偏移量映射
        Map<TopicPartition, Long> specificOffsets = new HashMap<>();
        specificOffsets.put(topicPartition, offset);

        // 创建 OffsetsInitializer
        OffsetsInitializer offsetsInitializer = OffsetsInitializer.offsets(specificOffsets);

        // 配置 Kafka Source
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
            .setBootstrapServers("your-bootstrap-servers:9092")
            .setTopics(topic)
            .setGroupId("your-group-id")
            .setStartingOffsets(offsetsInitializer)
            .setValueOnlyDeserializer(new SimpleStringSchema())
            .build();

        // 使用 Kafka Source 进行数据处理...
        // 例如，将其添加到 Flink 流执行环境中
        /*
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "Kafka Source")
            .print()
            .setParallelism(1);

        try {
            env.execute("Flink Kafka Source Example");
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }
}
