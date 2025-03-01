package com.juege.RiskCtrlSys.flink.job.map;

import com.juege.RiskCtrlSys.model.EventPO;
import com.juege.RiskCtrlSys.model.RulesPO;
import com.juege.RiskCtrlSys.model.SingleRulePO;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.List;

/**
 * author: Juege
 * description: 循环规则组, 将规则写入行为事件 (根据event_name)
 * date: 2024
 */

public class RulesFlatMap implements FlatMapFunction<EventPO, EventPO> {
    @Override
    public void flatMap(EventPO eventPO, Collector<EventPO> collector) throws Exception {
        //取出规则组
        RulesPO rules = eventPO.getRules();
        //最开始是 EventPO 的 singleRule 属性为空
        if(rules != null) {
            List<SingleRulePO> list = rules.getRules();
            //遍历规则组
            for(SingleRulePO rule:list) {
                //行为事件名 event_name
                String event_name = eventPO.getEvent_name();
                //规则适配的行为事件名 event_name_rule
                String event_name_rule = rule.getEvent_name();
                //event_name_rule是逗号分割的多个 event_name
                String[] event_name_list = event_name_rule.split(",");
                // 遍历规则适配的行为事件名
                for(String name:event_name_list) {
                    //行为事件和规则 根据 event_name 适配
                    if(event_name.equals(name)) {
                        eventPO.setSingleRule(rule);
                        //会产生冗余行为事件
                        collector.collect(eventPO);
                    }
                }
            }
        }
    }
}
