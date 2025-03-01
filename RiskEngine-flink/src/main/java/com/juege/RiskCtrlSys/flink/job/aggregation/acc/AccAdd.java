package com.juege.RiskCtrlSys.flink.job.aggregation.acc;

/**
 * author: Juege
 * description: 累加器计算: 直接+1
 * date: 2024
 */

public class AccAdd implements AccAggregate {

    public Double aggregate(
            String value_before,
            String value_after) {
        return 1.0;
    }
}
