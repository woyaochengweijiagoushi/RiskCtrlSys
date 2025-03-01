package com.juege.RiskCtrlSys.flink.job.aggregation.acc;

import com.juege.RiskCtrlSys.utils.date.DateUtil;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * author: Juege
 * description: 累加器计算: 时间间隔
 * date: 2024
 */

public class AccAverage implements AccAggregate {

    public Double aggregate(
            String value_before,
            String value_after) {
        //todo
        return null;
    }
}
