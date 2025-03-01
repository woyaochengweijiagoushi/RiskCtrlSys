package com.juege.RiskCtrlSys.flink.job.aviator;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;

/**
 * author: Juege
 * description: Aviator 自定义函数 (从Redis获取指标值)
 * date: 2024
 */

public class MetricRedisFunction extends AbstractFunction {

    private String fieldName;

    public MetricRedisFunction(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public AviatorObject call() throws Exception {

        //TODO: 从Redis获取指标值

        return super.call();


    }

    /**
     * author: Juege
     * description: 方法名
     * @param :
     * @return java.lang.String
     */
    @Override
    public String getName() {
        return "metricRedis";
    }
}
