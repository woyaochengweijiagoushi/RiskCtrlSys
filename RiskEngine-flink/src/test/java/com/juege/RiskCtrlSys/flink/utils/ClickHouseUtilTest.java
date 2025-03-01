package com.juege.RiskCtrlSys.flink.utils;

import com.juege.RiskCtrlSys.model.CHTestPO;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * author: Juege
 * description: flink clickhouse读写工具类单元测试
 * date: 2024
 */

public class ClickHouseUtilTest {

    @DisplayName("测试Flink+jdbc+游标读取Clickhouse")
    @Test
    void testRead() throws Exception {
        //初始化环境
        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();

        // 设置并行度1
        env.setParallelism(20);

        // 从default数据库的ch_juege_test表中读取数据
        String sql = "select * from default.ch_imooc_test";
        DataStream<CHTestPO> ds = ClickHouseUtil.read(env,sql);


        // 打印数据流中的元素
        ds.print("clickhouse");

        // 执行程序
        env.execute();

    }

    @DisplayName("测试Flink-Connector-jdbc+预编译批量写入Clickhouse")
    @Test
    void testBatchWrite() throws Exception {
        //初始化环境
        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();

        // 设置并行度1
        env.setParallelism(1);

        // 创建CHTestPO对象juegeTest1
        CHTestPO juegeTest1 = new CHTestPO();
        // 给juegeTest1的name属性赋值
        juegeTest1.setName("ch-juege-test-5");
        // 创建CHTestPO对象juegeTest2
        CHTestPO juegeTest2 = new CHTestPO();
        // 给juegeTest2的name属性赋值
        juegeTest2.setName("ch-juege-test-6");

        // 创建一个数据流ds，并将juegeTest1和juegeTest2添加到其中
        DataStream<CHTestPO> ds = env.fromCollection(Arrays.asList(juegeTest1, juegeTest2));

        // 定义将数据写入ClickHouse数据库的SQL语句
        String sql = "insert into default.ch_imooc_test(name) values(?)";
        // 调用ClickHouseUtil的batchWrite方法将数据流ds中的数据批量写入ClickHouse数据库
        ClickHouseUtil.batchWrite(ds, sql, 2);

        // 执行程序
        env.execute();

    }
}
