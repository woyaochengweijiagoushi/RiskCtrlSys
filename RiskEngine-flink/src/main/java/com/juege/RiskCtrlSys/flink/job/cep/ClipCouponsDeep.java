package com.juege.RiskCtrlSys.flink.job.cep;

import com.juege.RiskCtrlSys.flink.job.cep.condition.ClipCouponsDeep.BrowseCondition;
import com.juege.RiskCtrlSys.flink.job.cep.condition.ClipCouponsDeep.EventIntervalCondition;
import com.juege.RiskCtrlSys.flink.job.cep.condition.ClipCouponsDeep.GradeCondition;
import com.juege.RiskCtrlSys.flink.utils.KafkaUtil;
import com.juege.RiskCtrlSys.model.EventPO;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;

/**
 * author: Juege
 * description: 账号等级为L2以上的用户各行为之间时间间隔平均少于3分钟或者浏览行为停留平均时间少于3分钟
 * date: 2024
 */

public class ClipCouponsDeep {

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
                        .<EventPO>begin("before")
                        //账号等级为L2以上,才能进入模式
                        .where(new GradeCondition())
                        //前后行为事件,
                        .next("after")
                        //时间间隔平均少于3分钟
                        .where(new EventIntervalCondition())
                        //或者浏览行为停留平均时间少于3分钟
                        .or(new BrowseCondition());

    }
}
