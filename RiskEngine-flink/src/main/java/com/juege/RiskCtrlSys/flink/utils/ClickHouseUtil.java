package com.juege.RiskCtrlSys.flink.utils;

import com.juege.RiskCtrlSys.flink.clickhouse.sink.ClickHouseJdbcSink;
import com.juege.RiskCtrlSys.flink.clickhouse.source.ClickHouseSource;
import com.juege.RiskCtrlSys.model.CHTestPO;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * author: Juege
 * description: flink clickhouse读写工具类
 * date: 2024
 */

public class ClickHouseUtil<T> {

    private static String URL = null;

    static {
        ParameterTool parameterTool = ParameterUtil.getParameters();
        URL = parameterTool.get("clickhouse.url");

    }

    /**
     * author: Juege
     * description: 读取clickhouse
     * @param env:
     * @param sql:
     * @return org.apache.flink.streaming.api.datastream.DataStream<T>
     */
    public static  DataStream<CHTestPO> read(StreamExecutionEnvironment env, String sql) {
        return env.addSource(new ClickHouseSource(URL,sql));
    }


    /**
     * author: Juege
     * description: 批量写入ClickHouse
     * @param dataStream:
     * @param sql:
     * @param batchSize:
     * @return org.apache.flink.streaming.api.datastream.DataStreamSink<com.juege.RiskCtrlSys.model.CHTestPO>
     */
    public static <T> DataStreamSink<T> batchWrite(
            DataStream<T> dataStream,
            String sql,
            int batchSize) {

        //生成 SinkFunction

        ClickHouseJdbcSink<T> clickHouseJdbcSink =
                new ClickHouseJdbcSink<T>(sql,batchSize,URL);

        return dataStream.addSink(clickHouseJdbcSink.getSink());
    }

}
