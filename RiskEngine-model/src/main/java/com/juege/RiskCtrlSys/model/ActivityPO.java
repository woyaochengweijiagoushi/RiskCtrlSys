package com.juege.RiskCtrlSys.model;

import lombok.Data;

import java.util.List;

/**
 * author: Juege
 * description: 策略 POJO
 * date: 2024
 */

@Data
public class ActivityPO {

    /**
     * 策略名称
     */
    private String activation_name;

    /**
     * 策略动作
     */
    private List<ActionPO> actions;
}
