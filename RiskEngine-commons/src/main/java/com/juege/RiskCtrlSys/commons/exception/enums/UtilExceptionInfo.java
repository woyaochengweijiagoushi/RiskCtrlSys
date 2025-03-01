package com.juege.RiskCtrlSys.commons.exception.enums;

import lombok.Getter;

/**
 * author: Juege
 * description: 工具类异常信息枚举类
 * date: 2024
 */
@Getter
public enum UtilExceptionInfo implements BizExceptionInfo  {

    INVOKE_METHOD_NULL("-100", "反射方法执行错误");

    private String exceptionCode;
    private String exceptionMsg;

    UtilExceptionInfo(
            String exceptionCode,
            String exceptionMsg) {
        this.exceptionCode = exceptionCode;
        this.exceptionMsg = exceptionMsg;
    }

}
