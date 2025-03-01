package com.juege.RiskCtrlSys.commons.exception.enums;

/**
 * author: juege
 * description: 异常枚举类接口
 * date:  2024
*/

public interface BizExceptionInfo {

    /**
     * author: Juege
     * description: 获取异常错误码
     * @param :
     * @return java.lang.String
     */
    String getExceptionCode();

    /**
     * author: Juege
     * description: 获取异常信息
     * @param :
     * @return java.lang.String
     */
    String getExceptionMsg();
}
