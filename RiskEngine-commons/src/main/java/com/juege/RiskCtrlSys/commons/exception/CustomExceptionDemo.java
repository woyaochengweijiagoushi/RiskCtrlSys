package com.juege.RiskCtrlSys.commons.exception;

import com.juege.RiskCtrlSys.commons.exception.custom.RedisException;
import com.juege.RiskCtrlSys.commons.exception.enums.RedisExceptionInfo;

/**
 * author: Juege
 * description: 自定义异常类Demo
 * date: 2024
 */

public class CustomExceptionDemo {

    /**
     * author: Juege
     * description: 抛出自定义异常
     * @param :
     * @return void
     */
    public static void throwCustomException() throws RedisException {
        throw new RedisException(RedisExceptionInfo.REDISTEMPLATE_NULL);
    }
}
