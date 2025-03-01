package com.juege.RiskCtrlSys.flink.job;

import com.juege.RiskCtrlSys.commons.constants.ConstantsUtil;
import com.juege.RiskCtrlSys.flink.job.broadcast.MetricConfKeyedBroadcastProcessFunc;
import com.juege.RiskCtrlSys.flink.utils.DataStreamUtil;
import com.juege.RiskCtrlSys.flink.utils.KafkaUtil;
import com.juege.RiskCtrlSys.flink.utils.MysqlUtil;
import com.juege.RiskCtrlSys.flink.utils.ParameterUtil;
import com.juege.RiskCtrlSys.model.EventPO;
import com.juege.RiskCtrlSys.model.MetricsConfPO;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * author: Juege
 * description: Flink聚合计算框架基于指标配置参数进行计算 单元测试
 * date: 2024
 */

public class MetricConfStreamTest {


    @DisplayName("测试生成指标配置参数对象的配置流")
    @Test
    void testInitMetricConfStream() throws Exception {

        // 获取StreamExecutionEnvironment实例
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 获取参数工具
        ParameterTool parameterTool = ParameterUtil.getParameters();

        // 定义一个字符串变量tableName，用于存储Metrics属性的表名
        String tableName = ConstantsUtil.TABLE_NAME_METRIC_ATTR;

        // 获取规则ID
        int rule_id = 2;
        // 根据给定的表名和规则ID查询数据库中 is_enable 字段值为 'true' 的数据记录
        String sql = "SELECT * FROM " + tableName + " where is_enable='true' and rule_id=" + rule_id;

        // 从Mysql数据库获取指标配置参数对象的事件流
        DataStream<MetricsConfPO> metricConfStream = MysqlUtil.readWithTableOrSQLApi(
                env,
                parameterTool,
                MetricsConfPO.class,
                ConstantsUtil.TABLE_NAME_METRIC_ATTR,
                ConstantsUtil.DDL_METRIC_ATTR,
                sql
        );

        // 打印事件流中的数据
        metricConfStream.print();

        // 懒加载执行
        env.execute();

    }


    @DisplayName("测试通过广播流生成携带指标配置参数对象的事件流")
    @Test
    void testMakeMetricConfStream() throws Exception {


        // 获取参数
        ParameterTool parameterTool = ParameterUtil.getParameters();
        // 获取环境
        StreamExecutionEnvironment env = KafkaUtil.env;

        //############### kafka 行为事件流 ##########//

        // kafka 行为事件流
        DataStream<EventPO> kafkaStream = KafkaUtil.read(parameterTool);

        // 根据EventPO中的getUser_id_int方法获取整型的键值对，生成一个带有键的流
        KeyedStream<EventPO, Integer> keyedStream =
                kafkaStream.keyBy(EventPO::getUser_id_int);


        //############### mysql 指标属性配置流 ##########//

        String tableName = ConstantsUtil.TABLE_NAME_METRIC_ATTR;

        // 构建 SQL 查询语句，查询指定表中所有满足条件的记录
        String sql = "SELECT * FROM `" + tableName + "` where is_enable='true'";

        // 从Mysql数据库获取指标配置参数对象的配置流
        DataStream<MetricsConfPO> metricConfStream = MysqlUtil.readWithTableOrSQLApi(
                env,
                parameterTool,
                MetricsConfPO.class,
                ConstantsUtil.TABLE_NAME_METRIC_ATTR,
                ConstantsUtil.DDL_METRIC_ATTR,
                sql
        );

        //############## 两流合并  ###################//

        //建立MapState
        MapStateDescriptor<String, MetricsConfPO> mapState =
                new MapStateDescriptor<String, MetricsConfPO>(
                        "MetricBroadcastState",
                        BasicTypeInfo.STRING_TYPE_INFO,
                        TypeInformation.of(MetricsConfPO.class)
                );

        //广播流
        BroadcastStream<MetricsConfPO> broadcastStream =
                DataStreamUtil.broadcastStreamBuilder(
                        metricConfStream,
                        mapState
                );


        //合并流
        BroadcastConnectedStream<EventPO, MetricsConfPO> connectStream =
                DataStreamUtil.streamConnect(
                        keyedStream,
                        broadcastStream);


        //提取指标配置, 写入到事件流
        SingleOutputStreamOperator<EventPO> stream =
                DataStreamUtil.processFuncWithKey(
                        connectStream,
                        new MetricConfKeyedBroadcastProcessFunc(mapState)
                );


        //打印
        stream.print();

        // 懒加载执行
        env.execute();


    }



}
