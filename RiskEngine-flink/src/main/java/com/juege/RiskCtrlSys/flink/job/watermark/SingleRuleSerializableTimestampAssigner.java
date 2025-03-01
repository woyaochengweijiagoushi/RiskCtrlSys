package com.juege.RiskCtrlSys.flink.job.watermark;

import com.juege.RiskCtrlSys.model.SingleRulePO;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;

/**
 * author: Juege
 * description: 原子规则流自定义生成水印
 * date: 2024
 */

public class SingleRuleSerializableTimestampAssigner implements SerializableTimestampAssigner<SingleRulePO> {
    @Override
    public long extractTimestamp(SingleRulePO singleRulePO, long l) {
        return singleRulePO.getTs_ms();
    }
}
