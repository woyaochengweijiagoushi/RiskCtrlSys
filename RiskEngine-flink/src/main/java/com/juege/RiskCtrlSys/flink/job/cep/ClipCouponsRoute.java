package com.juege.RiskCtrlSys.flink.job.cep;

import com.juege.RiskCtrlSys.flink.job.cep.condition.ClipCouponsRoute.LoginCondition;
import com.juege.RiskCtrlSys.flink.job.cep.condition.ClipCouponsRoute.ReceiveCondition;
import com.juege.RiskCtrlSys.flink.job.cep.condition.ClipCouponsRoute.UseCondition;
import com.juege.RiskCtrlSys.flink.utils.KafkaUtil;
import com.juege.RiskCtrlSys.model.EventPO;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * author: Juege
 * description: 用户在15分钟内的行为路径是"登录-领券-下单"
 *               (明显薅羊毛行为特征)
 * date: 2024
 */

public class ClipCouponsRoute {
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
        Pattern<EventPO, ?> pattern =
                Pattern
                        //过滤登录行为事件
                        .<EventPO>begin("login").where(new LoginCondition())
                        //宽松近邻：过滤领取优惠券行为事件
                        .followedBy("receive").where(new ReceiveCondition())
                        //宽松近邻：过滤使用优惠券行为事件
                        .followedBy("use").where(new UseCondition())
                        //模式有效时间：15分钟内
                        .within(Time.minutes(15));
    }
}
