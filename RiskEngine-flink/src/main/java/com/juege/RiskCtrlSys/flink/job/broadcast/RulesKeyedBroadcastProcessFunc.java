package com.juege.RiskCtrlSys.flink.job.broadcast;

import com.juege.RiskCtrlSys.model.EventPO;
import com.juege.RiskCtrlSys.model.RulesPO;
import com.juege.RiskCtrlSys.model.SingleRulePO;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;

import java.util.List;

/**
 * author: Juege
 * description: 风控规则的 KeyedBroadcastProcessFunction
 * date: 2024
 */

public class RulesKeyedBroadcastProcessFunc
        extends KeyedBroadcastProcessFunction<Integer,EventPO,RulesPO,EventPO> {

    //规则流广播状态描述器
    private final MapStateDescriptor<String, RulesPO> stateDescriptor;

    //规则组唯一编码
    String the_set_code;

    //构造函数
    public RulesKeyedBroadcastProcessFunc(MapStateDescriptor<String, RulesPO> stateDescriptor, String the_set_code) {
        this.stateDescriptor = stateDescriptor;
        this.the_set_code = the_set_code;
    }

    /**
     * author: Juege
     * description: 处理非广播流的数据
     * @param eventPO:
     * @param readOnlyContext:
     * @param collector:
     * @return void
     */
    @Override
    public void processElement(
            EventPO eventPO,
            KeyedBroadcastProcessFunction<Integer, EventPO, RulesPO, EventPO>.ReadOnlyContext readOnlyContext,
            Collector<EventPO> collector) throws Exception {

        //都是只读操作
        ReadOnlyBroadcastState<String,RulesPO> broadcastState =
                readOnlyContext.getBroadcastState(stateDescriptor);


        if (broadcastState != null) {

            //取出广播状态的 规则组POJO
            RulesPO rules = broadcastState.get(the_set_code);
            if(rules != null) {
                eventPO.setRules(rules);
            }

        }

        //返回行为事件流
        collector.collect(eventPO);

    }

    /**
     * author: Juege
     * description: 处理广播流中的数据, 每次接收到广播流的每个记录都会调用
     * @param rulesPO:
     * @param context:
     * @param collector:
     * @return void
     */
    @Override
    public void processBroadcastElement(
            RulesPO rulesPO,
            KeyedBroadcastProcessFunction<Integer, EventPO, RulesPO, EventPO>.Context context,
            Collector<EventPO> collector) throws Exception {

        //获取广播状态
        BroadcastState<String,RulesPO> broadcastState = context.getBroadcastState(stateDescriptor);

        //判断广播状态是否存在对应的规则组
        //规则组唯一编码
        String set_code = rulesPO.getSet_code();
        //取出状态里的规则组
        RulesPO rulesState = broadcastState.get(set_code);
        if(rulesState == null) {
            //如果不存在规则组, 则将当前规则组放到广播状态
            broadcastState.put(set_code,rulesPO);
        }else {
            //规则
            List<SingleRulePO> rulesList = rulesPO.getRules();
            //状态里的规则
            List<SingleRulePO> rulesListState = rulesState.getRules();
            //添加规则到状态里的规则组
            rulesListState.add(rulesList.get(0));
            //
            rulesState.setRules(rulesListState);
            //更新广播状态
            broadcastState.put(set_code,rulesState);
        }

    }
}
