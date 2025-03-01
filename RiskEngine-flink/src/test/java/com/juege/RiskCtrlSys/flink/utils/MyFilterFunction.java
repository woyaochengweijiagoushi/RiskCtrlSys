package com.juege.RiskCtrlSys.flink.utils;

import com.juege.RiskCtrlSys.model.RedisPO;
import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.configuration.Configuration;

public class MyFilterFunction extends RichFilterFunction<RedisPO> {

    @Override
    public boolean filter(RedisPO redisPO) throws Exception {
        if("".equals(redisPO.getData())){
           return false;
        }
        System.out.println("filter:"+redisPO.getData());
        return true;
    }
}
