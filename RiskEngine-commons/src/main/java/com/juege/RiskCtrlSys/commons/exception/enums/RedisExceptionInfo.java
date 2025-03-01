package com.juege.RiskCtrlSys.commons.exception.enums;

import lombok.Getter;

/**
 * author: Juege
 * description: Redis异常枚举类
 * date: 2024
 */

@Getter
public enum RedisExceptionInfo implements BizExceptionInfo  {

    REDISTEMPLATE_NULL("-300", "RedisTemplate对象为null");

    private String exceptionCode;
    private String exceptionMsg;

    RedisExceptionInfo(String exceptionCode,
                       String exceptionMsg) {
        this.exceptionCode = exceptionCode;
        this.exceptionMsg = exceptionMsg;
    }
}
