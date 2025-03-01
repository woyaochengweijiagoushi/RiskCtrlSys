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
 * description: 基于组合模式检测最近1分钟内连续登录失败超过3次的用户
 *              CEP模式：这3次登录失败事件之间不允许出现其他行为事件
 * date: 2024
 */

public class LoginFailByComposite {

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

        /* **********************
         *
         * 知识点：
         *
         * 事件流：A(login_fail),B(login_fail),C(login_fail)。。。
         *
         * 1. 无论是 个体模式 还是 组合模式，模式定义的开头都是 begin()
         *
         * *********************/
        Pattern.
                <EventPO>begin("login_fail_first")
                .where(new SimpleCondition<EventPO>() {
                    @Override
                    public boolean filter(EventPO eventPO) throws Exception {
                        return EventConstantUtil.LOGIN_FAIL.equals(eventPO.getEvent_name());
                    }
                })
                .next("login_fail_second")
                .where(new SimpleCondition<EventPO>() {
                    @Override
                    public boolean filter(EventPO eventPO) throws Exception {
                        return EventConstantUtil.LOGIN_FAIL.equals(eventPO.getEvent_name());
                    }
                })
                .next("login_fail_third")
                .where(new SimpleCondition<EventPO>() {
                    @Override
                    public boolean filter(EventPO eventPO) throws Exception {
                        return EventConstantUtil.LOGIN_FAIL.equals(eventPO.getEvent_name());
                    }
                })
                .within(Time.seconds(60));

    }
}
