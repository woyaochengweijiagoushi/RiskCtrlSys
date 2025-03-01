package com.juege.RiskCtrlSys.flink.job.cep;

import com.juege.RiskCtrlSys.flink.job.cep.condition.IpChange.ChangeCondition;
import com.juege.RiskCtrlSys.flink.job.cep.select.IpChangeProcessFunction;
import com.juege.RiskCtrlSys.flink.utils.KafkaUtil;
import com.juege.RiskCtrlSys.model.EventPO;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * author: Juege
 * description: 基于迭代条件检测最近15分钟内IP更换次数超过3次的用户
 * date: 2024
 */

public class IpChangeByIterative {
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
         * 1.
         * 对于每个模式 (规则/Pattern),
         * 可以设置条件,判定到达的行为事件,是否能够进入到这个模式
         * 如：设置条件为, 只有登录成功这个行为事件, 才能够进入到这个模式
         *
         * 2.
         * 条件的设置方法是：where(),
         * where() 的参数是 IterativeCondition 对象
         *
         * 3
         * IterativeCondition 称为迭代条件,
         * 能够设置较复杂的条件,
         * 尤其和循环模式相结合
         *
         *
         *
         * *********************/

        Pattern<EventPO, ?> pattern =
        Pattern.
                //组合模式以begin开头,
                //不设置条件,所有行为事件都可以进入到这个模式
                <EventPO>begin("ip")
                //使用宽松近邻,判断用户行为事件在15分钟内IP是否发生变化
                .followedBy("next").where(new ChangeCondition())
                //15分钟内IP发生变化次数超过5次
                .times(3)
                //满足条件的行为事件必须在最近15分钟内
                .within(Time.seconds(900));


        //将模式应用到事件流
        /* **********************
         *
         * 知识点：
         *
         * 4
         * CEP.pattern(),
         * 还可以有第3个参数,
         * 第3个参数是比较器 EventComparator 对象,
         * 可以对于同时进入模式的行为事件,进行更精确的排序
         *
         * *********************/
        PatternStream<EventPO> patternStream = CEP.pattern(keyedStream, pattern);


        /* **********************
         *
         *
         * 知识点：
         *
         * 2.
         * 提取匹配事件的方法：
         * a. select()
         * b. flatSelect()
         * c. process()
         *
         * 3.
         * select() 和 flatSelect() 的区别：
         * flatSelect() 参数是 PatternFlatSelectFunction 对象
         * select() 参数是 PatternSelectFunction 对象
         * process() 参数是 PatternProcessFunction 对象
         *
         * flatSelect() 没有返回值, Collector.collect()
         * select() 有返回值
         * process() 没有返回值
         *
         * flatSelect() 可以通过 Collector.collect() 以事件流输出
         * process(),
         * 可以通过 Collector.collect() 以事件流输出
         * 也可以通过 Context 对象 获取上下文信息
         *
         * 建议使用 flatSelect(), 可以更加灵活
         * 官方建议使用 process()
         *
         *
         *
         * *********************/
        //提取匹配事件
        DataStream<EventPO> result = patternStream.process(new IpChangeProcessFunction());

        //执行规则命中的策略动作




      }
}
