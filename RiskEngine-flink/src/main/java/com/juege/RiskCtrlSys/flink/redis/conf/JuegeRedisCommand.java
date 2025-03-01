package com.juege.RiskCtrlSys.flink.redis.conf;

import lombok.Getter;

/**
 * author: Juege
 * description: Redis命令的枚举类
 * date: 2024
 */

@Getter
public enum JuegeRedisCommand {

    GET(JuegeRedisDataType.STRING);

    private JuegeRedisDataType juegeRedisDataType;

    JuegeRedisCommand(JuegeRedisDataType juegeRedisDataType) {
        this.juegeRedisDataType = juegeRedisDataType;
    }
}
