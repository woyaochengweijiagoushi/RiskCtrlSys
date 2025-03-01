package com.juege.RiskCtrlSys.flink.utils;

import com.juege.RiskCtrlSys.flink.model.MysqlTestPO;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public class MySinkFunction extends RichSinkFunction<MysqlTestPO> {

    @Override
    public void open(Configuration parameters) throws Exception {
    }

    @Override
    public void invoke(MysqlTestPO value, Context context) throws Exception {
        //同步到redis
    }
}
