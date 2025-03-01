package com.juege.RiskCtrlSys.flink.model;

import lombok.Data;

/**
 * author: Juege
 * description: 简单的行为事件POJO对象 (用于单元测试)
 * date: 2024
 */

@Data
public class SimpleEventTestPO {

    /**
     * 用户id
     */
    private Integer user_id_int;


    /**
     * 事件发生时间 (long)
     */
    private Long event_time;

    /**
     * 事件发生时间 (String)
     */
    private String event_time_string;

    /**
     * 事件名称
     */
    private String event_name;

    public SimpleEventTestPO(Integer user_id_int, String event_name, Long event_time) {
        this.user_id_int = user_id_int;
        this.event_time = event_time;
        this.event_name = event_name;
    }

    public SimpleEventTestPO(Integer user_id_int, String event_name, String event_time_string) {
        this.user_id_int = user_id_int;
        this.event_time_string = event_time_string;
        this.event_name = event_name;
    }
}
