package com.juege.RiskCtrlSys.flink.job.aggregation.acc;

import com.juege.RiskCtrlSys.utils.date.DateUtil;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * author: Juege
 * description: 累加器计算: 时间间隔
 * date: 2024
 */

public class AccTimeDiff implements AccAggregate {

    public Double aggregate(
            String value_before,
            String value_after) {

        LocalDateTime local1 =DateUtil.convertStr2LocalDateTime(value_before);
        LocalDateTime local2 =DateUtil.convertStr2LocalDateTime(value_after);

        Duration dur= Duration.between(local1,local2);
        long minutes = dur.toMinutes();

        double second = minutes*60;
        return second;
    }
}
