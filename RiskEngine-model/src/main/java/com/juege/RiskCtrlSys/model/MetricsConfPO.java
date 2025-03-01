package com.juege.RiskCtrlSys.model;

import lombok.Data;

/**
 * author: Juege
 * description: 指标配置参数POJO对象
 * date: 2024
 */

@Data
public class MetricsConfPO {

    /* **********************
     *
     * 注意：
     *
     * 1.
     * 属性名称必须和 Mysql 表 metric_attr 的字段名称一致
     *
     * 2.
     * 为了后面反射赋值的方便,
     * 属性数据类型都是 String
     *
     *
     * *********************/



    /**
     * 指标名称
     */
    private String metric_name;
    private Long metric_id;
    private Long rule_id;
    /**
     * 指标编码
     */
    private String metric_code;
    /**
     * 指标值存储路径
     */
    private String metric_store;
    /**
     * 主维度
     */
    private String main_dim;
    /**
     * flink窗口大小
     */
    private String window_size;
    /**
     * flink窗口大小
     */
    private String window_step;
    /**
     * flink窗口类型
     */
    private String window_type;
    /**
     * flink筛选
     */
    private String flink_filter;
    /**
     * flink分组
     */
    private String flink_keyby;
    /**
     * flink水印
     */
    private String flink_watermark;
    /**
     * 指标数据源
     */
    private String datasource;
    /**
     * flink 聚合计算方式
     */
    private String metric_agg_type;

    /**
     * flink 聚合计算
     */
    private String aggregation;
    private String is_enable;
    private String scene;
    private String event;
}
