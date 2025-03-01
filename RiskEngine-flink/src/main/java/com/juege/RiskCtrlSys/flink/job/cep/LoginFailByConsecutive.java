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
 * description: 基于个体模式检测最近1分钟内连续登录失败超过3次的用户
 *              CEP模式：这3次登录失败事件之间不允许出现其他行为事件
 * date: 2024
 */

public class LoginFailByConsecutive {

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
                .where(new SimpleCondition<EventPO>() {
                    @Override
                    public boolean filter(EventPO eventPO) throws Exception {
                        return EventConstantUtil.LOGIN_FAIL.equals(eventPO.getEvent_name());
                    }
                })
                /* **********************
                 *
                 * 知识点：
                 *
                 * 事件流1(连续登录失败的事件流)：event_A(login_fail),event_B(login_fail),event_C(login_fail)
                 * 事件流2(不连续登录失败的事件流)：event_A(login_fail),event_D(login_success),event_B(login_fail),event_C(login_fail)
                 *
                 * 宽松近邻：不连续事件
                 * 严格紧邻：连续事件
                 *
                 * 1. 个体模式的循环模式 匹配的是 宽松近邻
                 *
                 * 2. consecutive() 就指定匹配模式是 严格紧邻
                 *
                 *
                 *
                 * *********************/
                .times(3)
//                .consecutive()
                .within(Time.seconds(60));

    }
}
