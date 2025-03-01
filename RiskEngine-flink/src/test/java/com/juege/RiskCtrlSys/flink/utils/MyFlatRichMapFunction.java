package com.juege.RiskCtrlSys.flink.utils;


import com.juege.RiskCtrlSys.model.RedisPO;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.util.Collector;

public class MyFlatRichMapFunction extends RichFlatMapFunction<RedisPO, RedisPO> {

    @Override
    public void flatMap(RedisPO redisPO, Collector<RedisPO> collector) throws Exception {

        RedisPO redisPO1 = new RedisPO();
        redisPO1.setData(redisPO.getData()+"111");

        collector.collect(redisPO);
        collector.collect(redisPO1);
    }
}