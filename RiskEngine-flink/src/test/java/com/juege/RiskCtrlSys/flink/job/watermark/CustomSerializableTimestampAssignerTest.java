package com.juege.RiskCtrlSys.flink.job.watermark;

import com.juege.RiskCtrlSys.flink.model.MysqlTestPO;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;

/**
 * author: Juege
 * description: TODO
 * date: 2024
 */

public class CustomSerializableTimestampAssignerTest implements SerializableTimestampAssigner<MysqlTestPO> {
    @Override
    public long extractTimestamp(MysqlTestPO mysqlTestPO, long l) {
        return mysqlTestPO.getTs_ms();
    }
}
