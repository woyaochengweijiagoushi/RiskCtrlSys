package com.juege.RiskCtrlSys.commons.exception.custom;

import com.juege.RiskCtrlSys.commons.exception.BizRuntimeException;
import com.juege.RiskCtrlSys.commons.exception.enums.BizExceptionInfo;

/**
 * author: Juege
 * description: 工具类自定义错误
 * date: 2024
 */

public class UtilException extends BizRuntimeException {
    /**
     * author: Juege
     * description: 自定义异常类构造方法
     *
     * @param info :  自定义异常枚举对象
     * @return null
     */
    public UtilException(BizExceptionInfo info) {
        super(info);
    }
}
