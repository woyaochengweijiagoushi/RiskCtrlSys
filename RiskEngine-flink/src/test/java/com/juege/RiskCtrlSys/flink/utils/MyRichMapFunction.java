package com.juege.RiskCtrlSys.flink.utils;


import com.juege.RiskCtrlSys.model.RedisPO;
import org.apache.flink.api.common.functions.RichMapFunction;

public class MyRichMapFunction extends RichMapFunction<RedisPO, RedisPO> {


    @Override
    public RedisPO map(RedisPO redisPO) throws Exception {
            redisPO.setData("ddddd!!!!!!!!!");
        System.out.println("value transform to"+redisPO.getData());
        return redisPO;
    }
}