package com.juege.RiskCtrlSys.flink.job.cep;

import com.juege.RiskCtrlSys.flink.utils.EventConstantUtil;
import com.juege.RiskCtrlSys.flink.utils.KafkaUtil;
import com.juege.RiskCtrlSys.model.EventPO;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * author: Juege
 * description: 基于个体模式检测最近1分钟内登录失败超过3次的用户
 *              CEP模式：允许这3次登录失败事件之间出现其他行为事件
 * date: 2024
 */

public class LoginFailBySingleton {

    public static void main(String[] args) {
        // Kafka
        DataStream<EventPO> eventStream = KafkaUtil.read(args);

        //生成KeyedStream
        KeyedStream<EventPO, Integer> keyedStream = eventStream.keyBy(new KeySelector<EventPO, Integer>() {
            @Override
            public Integer getKey(EventPO eventPO) throws Exception {
                return eventPO.getUser_id_int();
            }
        });

        //生成模式 (规则/Pattern)

        Pattern.
                <EventPO>begin("login_fail_first")
                /* **********************
                 *
                 * 知识点：
                 *
                 * 1. IterativeCondition 抽象类 表示通用的匹配规则
                 * 需要实现 filter(), 需要传入2个参数
                 *
                 * 2.SimpleCondition 是 IterativeCondition 的子类，表示简单的匹配规则
                 * 需要实现 filter(), 需要传入1个参数
                 *
                 *
                 * *********************/
                .where(new SimpleCondition<EventPO>() {
                    @Override
                    public boolean filter(EventPO eventPO) throws Exception {
                        return EventConstantUtil.LOGIN_FAIL.equals(eventPO.getEvent_name());
                    }
                })
                .times(3)
                .within(Time.seconds(60));

    }
}
