package com.juege.RiskCtrlSys.model;

import lombok.Data;

/**
 * author: Juege
 * description: Kafka消息数据PO对象
 * date: 2024
 */

@Data
public class KafkaMessagePO {

    /**
     * 事件id
     */
    private String event_id;
    /**
     * 用户id (String)
     */
    private String user_id_str;
    /**
     * 用户id (int)
     */
    private Integer user_id_int;

    /**
     * 事件动作名称
     */
    private String event_behavior_name;
        /**
     * 事件动作id
     */
    private Integer event_behavior_id;
    /**
     * 事件目标id
     */
    private Integer event_target_id;
    /**
     * 事件目标名称
     */
    private String event_target_name;
    /**
     * 事件类型
     */
    private String event_type;
    /**
     * 事件等级
     */
    private String event_level;
    /**
     * 事件名称
     */
    private String event_name;
    /**
     * 事件接入来源
     */
    private String event_source;
    /**
     * 事件发生时间
     */
    private String event_time;
    /**
     * 事件上下文
     */
    private EventContextPO event_context;

    /**
     * kafka offset
     */
    private long offset;
    /**
     * kafka partition
     */
    private int partition;

}
