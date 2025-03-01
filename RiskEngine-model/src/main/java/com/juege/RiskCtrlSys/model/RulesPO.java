package com.juege.RiskCtrlSys.model;

//import com.juege.RiskCtrlSys.service.activity.Activity;
import lombok.Data;

import java.util.List;

/**
 * author: Juege
 * description: 规则组 POJO 对象
 * date: 2024
 */

/* **********************
 *
 * 注意：
 *
 * 对行为事件应用风控规则,
 * 是应用规则组, 不是单条规则,
 * 所以即使只有1条规则, 也要放到规则组里
 *
 * *********************/

@Data
public class RulesPO {

    /**
     * 规则列表
     */
    private List<SingleRulePO> rules;

    /**
     * 规则唯一编码
     */
    private String rule_code;

    /**
     * 规则组唯一编码
     */
    private String set_code;

    /**
     * 规则组名称
     */
    private String rule_set_name;

    /**
     * 策略
     */
    private ActivityPO activity;

    /**
     * 行为事件列表
     */
    private List<String> eventNames;

    /**
     * Mysql操作时间戳 (用于生成水印)
     */
    private long ts_ms;
}
