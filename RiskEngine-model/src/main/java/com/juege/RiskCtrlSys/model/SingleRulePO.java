package com.juege.RiskCtrlSys.model;

import lombok.Data;

/**
 * author: Juege
 * description: 单条规则 POJO 对象
 * date: 2024
 */

@Data
public class SingleRulePO {


    /**
     * 规则唯一标识
     */
    private String rule_code;

    /**
     * 规则名称
     */
    private String rule_name;

    /**
     * 适用规则的行为事件 (多个以,隔开)
     */
    private String event_name;

    /**
     * 规则是否开启
     */
    private String is_enable;

    /**
     * 条件表达式
     */
    private String expression;


    /**
     * 策略动作方法
     */
    private String action;

    /**
     * Mysql操作时间戳 (用于生成水印)
     */
    private long ts_ms;

    /**
     * 无参构造函数
     */
    public SingleRulePO() {
    }

    /**
     * 构造函数
     * @param rule_code
     * @param rule_name
     * @param event_name
     * @param is_enable
     */
    public SingleRulePO(String rule_code, String rule_name, String event_name, String is_enable) {
        this.rule_code = rule_code;
        this.rule_name = rule_name;
        this.event_name = event_name;
        this.is_enable = is_enable;
    }
}
