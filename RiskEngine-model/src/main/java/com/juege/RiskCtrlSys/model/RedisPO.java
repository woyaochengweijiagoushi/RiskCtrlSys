package com.juege.RiskCtrlSys.model;

import lombok.Data;

/**
 * author: Juege
 * description: Redis数据的映射对象
 * date: 2024
 */

@Data
public class RedisPO {

    /**
     * Redis 值
     */
    private String data;

    public RedisPO() {
    }

    public RedisPO(String data) {
        this.data = data;
    }
}
