package com.juege.RiskCtrlSys.flink.redis.conf;

import lombok.Getter;

/**
 * author: Juege
 * description: Redis数据类型的枚举类
 * date: 2024
 */

@Getter
public enum JuegeRedisDataType {

    STRING,
    HASH,
    LIST,
    SET,
    SORTED_SET,
    ;

    JuegeRedisDataType() {
    }
}
