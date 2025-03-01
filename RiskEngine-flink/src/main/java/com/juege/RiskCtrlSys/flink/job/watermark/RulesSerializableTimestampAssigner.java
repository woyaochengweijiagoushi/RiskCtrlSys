package com.juege.RiskCtrlSys.flink.job.watermark;

import com.juege.RiskCtrlSys.model.RulesPO;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;

/**
 * author: Juege
 * description: 规则组流自定义生成水印
 * date: 2024
 */

public class RulesSerializableTimestampAssigner implements SerializableTimestampAssigner<RulesPO> {
    @Override
    public long extractTimestamp(RulesPO rulesPO, long l) {
        return rulesPO.getTs_ms();
    }
}
