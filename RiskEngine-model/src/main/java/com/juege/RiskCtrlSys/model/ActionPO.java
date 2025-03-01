package com.juege.RiskCtrlSys.model;

import lombok.Data;

/**
 * author: Juege
 * description: 策略动作 POJO
 * date: 2024
 */

@Data
public class ActionPO {

    /**
     * 策略动作方法
     */
    private String action;
    /**
     * 风险信息
     */
    private RiskInfoPO info;
}
