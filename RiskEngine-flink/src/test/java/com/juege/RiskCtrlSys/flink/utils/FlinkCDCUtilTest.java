package com.juege.RiskCtrlSys.flink.utils;

import com.juege.RiskCtrlSys.flink.job.cdc.CustomDebeziumDeserializerTest;
import com.juege.RiskCtrlSys.flink.job.cdc.PojoDebeziumDeserializerTest;
import com.juege.RiskCtrlSys.flink.job.watermark.CustomSerializableTimestampAssignerTest;
import com.juege.RiskCtrlSys.flink.model.MysqlTestPO;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Properties;

/**
 * author: Juege
 * description: Flink-CDC 工具类单元测试
 * date: 2024
 */

public class FlinkCDCUtilTest{


    @DisplayName("测试Flink-CDC 捕获mysql数据变更 (包括内置序列化和自定义序列化)")
    @Test
    void testMysqlCDCSource() throws Exception {

        // 初始化环境
        StreamExecutionEnvironment env = DataStreamUtil.initEnv();
        // 设置并行度1
        env.setParallelism(1);

        // 获取参数工具
        ParameterTool parameterTool = ParameterUtil.getParameters();

        // 创建属性对象
        Properties properties = new Properties();

        //监听的数据库
        String cdc_database_name = parameterTool.get(ParameterConstantsUtil.FLINK_CDC_MYSQL_DATABASE);
        //监听的表要要加上库名
        String cdc_table_name = cdc_database_name+".mysql_test";
        // 使用内置的反序列化
        //DebeziumDeserializationSchema<String> deserializer = new JsonDebeziumDeserializationSchema();
        //使用自定义的反序列化
        DebeziumDeserializationSchema<String> deserializer = new CustomDebeziumDeserializerTest();

        // 创建MySQL-CDC源
        MySqlSource<String> mySqlSource =
                FlinkCDCUtil.getMysqlCDCSource(
                        parameterTool,
                        properties,
                        deserializer,
                        cdc_table_name);

        // 从源获取数据流并打印
        env
            .fromSource(
                mySqlSource,
                WatermarkStrategy.noWatermarks(),
                "mysql-cdc-source-test")
            .print("mysql-cdc");

        // 执行环境
        env.execute();

    }


    @DisplayName("测试Flink-CDC自定义序列化为指定POJO对象并带上水印")
    @Test
    void testDeserialize() throws Exception {

        // 初始化环境
        StreamExecutionEnvironment env = DataStreamUtil.initEnv();
        // 设置并行度1
        env.setParallelism(1);
        // 获取参数工具
        ParameterTool parameterTool = ParameterUtil.getParameters();
        // 创建属性对象
        Properties properties = new Properties();
        //监听的数据库
        String cdc_database_name = parameterTool.get(ParameterConstantsUtil.FLINK_CDC_MYSQL_DATABASE);
        //监听的表要要加上库名
        String cdc_table_name = cdc_database_name+".mysql_test";
        //使用自定义的反序列化
        DebeziumDeserializationSchema<MysqlTestPO> deserializer = new PojoDebeziumDeserializerTest();

        // 创建MySQL-CDC源
        MySqlSource<MysqlTestPO> mySqlSource =
                FlinkCDCUtil.getMysqlCDCSource(
                        parameterTool,
                        properties,
                        deserializer,
                        cdc_table_name);

        // 从源获取数据流并打印
        DataStreamSource<MysqlTestPO> mysqlTestPODataStreamSource = env.fromSource(
                mySqlSource,
                //WatermarkStrategy.noWatermarks(),
                WatermarkStrategy
                        .<MysqlTestPO>forBoundedOutOfOrderness(Duration.ofSeconds(1L))
                        .withTimestampAssigner(new CustomSerializableTimestampAssignerTest()),
                "mysql-cdc-pojo-test");
//                .print("cdc-pojo");

        mysqlTestPODataStreamSource.addSink(new MySinkFunction());

        // 执行环境
        env.execute();

    }
}
