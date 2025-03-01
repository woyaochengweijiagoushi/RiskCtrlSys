package com.juege.RiskCtrlSys.flink.utils;

import com.juege.RiskCtrlSys.flink.model.MysqlTestPO;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * author: Juege
 * description: Flink Mysql读写工具类单元测试
 * date: 2024
 */

public class MysqlUtilTest {

    @DisplayName("测试使用 Flink Table/SQL Api 读取Mysql")
    @Test
    public void testReadWithTableOrSQLApi() throws Exception {

        // 初始化环境
        StreamExecutionEnvironment env = DataStreamUtil.initEnv();
        // 设置并行度1
        env.setParallelism(1);
        // 获取参数工具实例
        ParameterTool parameterTool = ParameterUtil.getParameters();


        /* **********************
         *
         * CREATE 语句用于向当前或指定的 Catalog 中注册表。
         * 注册后的表、视图和函数可以在 SQL 查询中使用
         *
         * *********************/
        // 表名
        String tableName = "mysql_test";

        // 表字段ddl
        String ddlFieldString =
                    ""+
                    "id BIGINT,\n"+
                    "name STRING,\n"+
                    "ts_ms BIGINT \n"
                    ;

        // 查询表的全部字段
        String sql = "SELECT * FROM "+tableName;

        // 调用MysqlUtil类的readWithTableOrSQLApi方法，读取MySQL数据库的数据并返回一个DataStream对象
        // DataStream对象的泛型参数为MysqlTestPO类
        DataStream<MysqlTestPO> rowDataStream =
                MysqlUtil.readWithTableOrSQLApi(
                        env,
                        parameterTool,
                        MysqlTestPO.class,
                        tableName,
                        ddlFieldString,
                        sql
                        );


        // 对rowDataStream调用print方法，打印数据
        rowDataStream.print("mysql");

        // 启动应用程序的环境，开始执行Flink程序
        env.execute();
    }
}
