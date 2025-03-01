package com.juege.RiskCtrlSys.flink.job.join;

import com.juege.RiskCtrlSys.model.RulesPO;
import com.juege.RiskCtrlSys.model.SingleRulePO;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Juege
 * description: 双流Join (原子规则表 Join 规则组表)
 * date: 2024
 */

public class RulesProcessJoinFunction extends ProcessJoinFunction<RulesPO, SingleRulePO, RulesPO> {
    /**
     * author: Juege
     * description: Join 的处理逻辑
     * @param rulesPO:
     * @param singleRulePO:
     * @param context:
     * @param collector:
     * @return void
     */
    @Override
    public void processElement(
            RulesPO rulesPO,
            SingleRulePO singleRulePO,
            ProcessJoinFunction<RulesPO, SingleRulePO, RulesPO>.Context context,
            Collector<RulesPO> collector) throws Exception {

        //将 SingleRulePO 写入 RulesPO
        List<SingleRulePO> rules = rulesPO.getRules();
        if(rules == null) {
            rules = new ArrayList<>();
        }
        rules.add(singleRulePO);
        rulesPO.setRules(rules);
        //输出
        collector.collect(rulesPO);

    }
}
