package com.juege.RiskCtrlSys.commons.exception.custom;

import com.juege.RiskCtrlSys.commons.exception.BizRuntimeException;
import com.juege.RiskCtrlSys.commons.exception.enums.BizExceptionInfo;

/**
 * author: Juege
 * description: Redis 自定义异常类
 * date: 2024
 */

public class RedisException extends BizRuntimeException {


    public RedisException(BizExceptionInfo info) {
        super(info);
    }
}
