package com.juege.RiskCtrlSys.model;

import lombok.Data;

/**
 * author: Juege
 * description: 用户事件行为的POJO对象
 * date: 2024
 */

//@Data
public class EventPO2 {

    /* **********************
     *
     * 注意：
     *
     * 属性名称要和写入到Mysql的属性名称一致
     *
     *         /* **********************
     *
     * 知识点：
     *
     * 1.
     * 如果返回的是PojoType,
     * 则对象会被Flink视为POJO对象,
     * 序列化使用的是 PojoSerializer
     * 并且序列化的时候, 就只会序列化字段, 方法不会被序列化
     * 反序列化的时候, 会根据公有的无参构造方法, 去构造对象
     *
     * 2.
     * 如果返回的是GenericType，
     * 则对象不会被Flink视为POJO对象,
     * 序列化使用的就是 kryo
     *
     *
     *
     * *********************/


    /**
     * 用户id
     */
    private Integer user_id_int;

    /**
     * 事件发生时间
     */
    private String event_time;


    /**
     * 事件目标名称
     */
    private String event_target_name;

    /**
     * 事件名称
     */
    private String event_name;
    /**
     * 事件类型
     */
    private String event_type;
    /**
     * 事件上下文
     */
    private EventContextPO event_context;

    /**
     * 指标各配置参数
     */
    private MetricsConfPO metrics_conf;

    /**
     * 原子规则
     */
    private SingleRulePO singleRule;

    /**
     * 规则组
     */
    private RulesPO rules;

    /**
     * kafka offset
     */
    private long offset;
    /**
     * kafka partition
     */
    private int partition;

    public EventPO2() {
    }

    public EventPO2(
            Integer user_id_int,
            String event_time,
            String event_target_name,
            String event_name,
            String event_type,
            EventContextPO event_context) {
        this.user_id_int = user_id_int;
        this.event_time = event_time;
        this.event_target_name = event_target_name;
        this.event_name = event_name;
        this.event_type = event_type;
        this.event_context = event_context;
    }

}
