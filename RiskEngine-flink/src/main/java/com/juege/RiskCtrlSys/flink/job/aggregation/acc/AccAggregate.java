package com.juege.RiskCtrlSys.flink.job.aggregation.acc;

/**
 * author: Juege
 * description: 累加器计算方法接口
 * date: 2024
 */

public interface AccAggregate {

    public Double aggregate(String value_before, String value_after);
}
