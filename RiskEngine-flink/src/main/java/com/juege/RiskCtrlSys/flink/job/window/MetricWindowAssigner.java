package com.juege.RiskCtrlSys.flink.job.window;


import com.juege.RiskCtrlSys.model.EventPO;
import com.juege.RiskCtrlSys.utils.date.DateUtil;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * author: Juege
 * description: 指标 window 通用模块
 * date: 2024
 */

/* **********************
 *
 *
 * 为什么要自定义 窗口分配器 ?
 *
 * 按天、小时、分钟的滚动或滑动窗口很容易实现。
 * 直接调用Flink内置API
 * SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5)),
 * 这是每5秒计算10秒内的数据,
 *
 * 但如果要计算的窗口是 "每天 6:00 ~ 第二天的 18:00" 的数据:
 * 窗口时间范围 每天 6:00 ~ 第二天的 18:00 (36小时)
 * 窗口大小：12h
 * 窗口步长: 1h
 * 窗口类型: 滑动窗口
 * Flink内置API很难实现,
 * 需要自定义 窗口分配器
 *
 *
 * *********************/


public class MetricWindowAssigner extends WindowAssigner<EventPO, TimeWindow> {


    // 窗口大小
    private long size;
    // 窗口步长
    private long step;
    private final long offset = 0L;


    /* **********************
     *
     * 目标：
     *
     * 实现任意时间的滑动窗口分配器
     *
     * 如实现按周划分窗口, 即指标计算的目标时间段
     * 如周日的 00:00:00 到 周六的 23:59:59
     *
     *
     * *********************/


    /**
     * author: Juege
     * description: 将带有时间戳的行为事件 分配给1个或多个窗口,返回的是1个单例窗口集合
     * @param eventPO:
     * @param timestamp: 行为事件的时间戳
     * @param windowAssignerContext:  窗口上下文
     * @return java.util.Collection<org.apache.flink.streaming.api.windowing.windows.TimeWindow> 
     */

    @Override
    public Collection<TimeWindow> assignWindows(
            EventPO eventPO,
            long timestamp,
            WindowAssignerContext windowAssignerContext) {

        /* **********************
         *
         * 需要解决的问题：
         *
         * 1. 窗口的开始时间和结束时间怎么计算 ?
         * 2. 数据被分到几个窗口 ? 对于滑动窗口, 同一条数据会被分配到多个窗口
         *
         * 思路：
         *
         * 1. 窗口的开始时间 通过 TimeWindow.getWindowStartWithOffset() 确定
         * 2. 窗口的开始时间确定后, 窗口的结束时间也就确定
         * 3. 数据被分到的窗口数量 = 窗口的长度 / 窗口滑动的步长
         *
         *
         * 建议仿造 SlidingEventTimeWindows 的实现方法
         *
         * *********************/


        //获取指标配置参数
        //窗口大小
        String _size = eventPO.getMetrics_conf().getWindow_size();
        //转换为毫秒
        size = Long.parseLong(_size) * 1000L;
        //窗口步长
        String _step = eventPO.getMetrics_conf().getWindow_step();
        //转换为毫秒
        step = Long.parseLong(_step) * 1000L;


        //获取指标配置参数
        //窗口开始时间
        String _winStart = eventPO.getMetrics_conf().getFlink_filter();
        //窗口结束时间
        String _winEnd = eventPO.getMetrics_conf().getFlink_filter();
        //窗口持续天数
        String _days = eventPO.getMetrics_conf().getFlink_filter();


        //判断是否设置了窗口固定时间范围
        if(_winStart.equals("0")&&_winEnd.equals("0")&&_days.equals("0")) {
            /* 没有设置窗口固定时间范围 */
            return windowNoDaysRange(timestamp);
        }else {
            /* 设置有窗口固定时间范围 */


            //窗口开始时间 LocalDateTime
            LocalDateTime _winStart_l = DateUtil.convertStr2LocalDateTime(_winStart);
            //窗口开始时间 增加天数
            LocalDateTime _plus_days = DateUtil.localDateTimePlusDays(_winStart_l, _days);
            //窗口开始时间 增加秒
            LocalDateTime _winEnd_l = DateUtil.localDateTimePlusSec(_plus_days, _winEnd);

            //转换为时间戳
            long winStart = DateUtil.convertLocalDateTime2Timestamp(_winStart_l);
            long winEnd = DateUtil.convertLocalDateTime2Timestamp(_winEnd_l);

            //获取当前行为事件所属窗口的开始时间
            long lastStart = TimeWindow.getWindowStartWithOffset(
                    timestamp,
                    this.offset,
                    this.step);

            //获取当前行为事件所属窗口的结束时间
            long lastEnd = lastStart + step;

            return windowWithDaysRange(timestamp,lastStart,lastEnd,winEnd);

        }


    }

    /**
     * author: Juege
     * description: WindowAssigner默认的 触发器
     * @param streamExecutionEnvironment:
     * @return org.apache.flink.streaming.api.windowing.triggers.Trigger<com.juege.RiskCtrlSys.model.EventPO,org.apache.flink.streaming.api.windowing.windows.TimeWindow> 
     */
    @Override
    public Trigger<EventPO, TimeWindow> getDefaultTrigger(
            StreamExecutionEnvironment streamExecutionEnvironment) {
        return null;
    }

    /**
     * author: Juege
     * description: 返回一个类型序列化器用来序列化窗口
     * @param executionConfig:
     * @return org.apache.flink.api.common.typeutils.TypeSerializer<org.apache.flink.streaming.api.windowing.windows.TimeWindow> 
     */
    @Override
    public TypeSerializer<TimeWindow> getWindowSerializer(ExecutionConfig executionConfig) {
        return new TimeWindow.Serializer();
    }

    /**
     * author: Juege
     * description: 是否使用 EventTime 时间语义
     * @param :
     * @return boolean 
     */
    @Override
    public boolean isEventTime() {
        return true;
    }

    /**
     * author: Juege
     * description: 没有设置窗口固定时间范围
     * @param timestamp:
     * @return java.util.Collection<org.apache.flink.streaming.api.windowing.windows.TimeWindow>
     */
    private Collection<TimeWindow> windowNoDaysRange(long timestamp) {

        if (timestamp <= Long.MIN_VALUE) {
            throw new RuntimeException("Record has Long.MIN_VALUE timestamp (= no timestamp marker). Is the time characteristic set to 'ProcessingTime', or did you forget to call 'DataStream.assignTimestampsAndWatermarks(...)'?");
        } else {
            List<TimeWindow> windows = new ArrayList((int)(this.size / this.step));
            long lastStart = TimeWindow.getWindowStartWithOffset(timestamp, this.offset, this.step);

            for(long start = lastStart; start > timestamp - this.size; start -= this.step) {
                windows.add(new TimeWindow(start, start + this.size));
            }

            return windows;
        }
    }


    /**
     * author: Juege
     * description: 设置有窗口固定时间范围
     * @param timestamp:
     * @param lastStart:
     * @param lastEnd:
     * @param winEnd:
     * @return java.util.Collection<org.apache.flink.streaming.api.windowing.windows.TimeWindow>
     */
    private Collection<TimeWindow> windowWithDaysRange(
            long timestamp,
            long lastStart,
            long lastEnd,
            long winEnd
    ) {

        if (timestamp > Long.MIN_VALUE) {


            //获取每条行为事件被分到的窗口数量
            int winCounts = (int)((winEnd-lastEnd) / this.step);
            List<TimeWindow> windows = new ArrayList<TimeWindow>(winCounts);

            while (lastEnd < winEnd) {
                windows.add(new TimeWindow(lastStart, lastEnd));
                lastEnd += step;
            }
            return windows;
        }else {
            throw new RuntimeException("Record has Long.MIN_VALUE timestamp (= no timestamp marker). Is the time characteristic set to 'ProcessingTime', or did you forget to call 'DataStream.assignTimestampsAndWatermarks(...)'?");
        }

    }
}
