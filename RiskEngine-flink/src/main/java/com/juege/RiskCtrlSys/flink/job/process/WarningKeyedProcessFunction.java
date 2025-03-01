package com.juege.RiskCtrlSys.flink.job.process;

import com.juege.RiskCtrlSys.commons.constants.ConstantsUtil;
import com.juege.RiskCtrlSys.flink.job.aviator.MetricRedisFunction;
import com.juege.RiskCtrlSys.flink.utils.AviatorUtil;
import com.juege.RiskCtrlSys.model.ActionPO;
import com.juege.RiskCtrlSys.model.EventPO;
import com.juege.RiskCtrlSys.model.RiskInfoPO;
import com.juege.RiskCtrlSys.model.SingleRulePO;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/**
 * author: Juege
 * description: 规则判断
 * date: 2024
 */

public class WarningKeyedProcessFunction extends KeyedProcessFunction<Integer, EventPO, ActionPO> {
    /**
     * author: Juege
     * description: 业务逻辑 (输出的是规则命中后的动作)
     * @param eventPO:
     * @param context:
     * @param collector:
     * @return void
     */
    @Override
    public void processElement(EventPO eventPO, KeyedProcessFunction<Integer, EventPO, ActionPO>.Context context, Collector<ActionPO> collector) throws Exception {

        //取出规则
        SingleRulePO rule = eventPO.getSingleRule();
        String is_enable = rule.getIs_enable();
        //判断规则是否启用
        if(is_enable.equals("true")) {
            //取出规则的表达式 (包含了指标,关系运算符,阈值)
            String expression = rule.getExpression();
            //将表达式按|分割成数组 格式：函数|运算符和阈值 (例：metricRedis(register)|>50, 注: register是Redis的key名)
            String[] arg = expression.split("\\|");
            /* **********************
             *
             * 使用aviator对表达式进行解析
             *
             * *********************/
            //反射得到Aviator自定义函数实例
            Class<?> clazz = Class.forName(ConstantsUtil.PATH_CLASS_METRIC_REDIS_FUNCTION);
            MetricRedisFunction metricRedisFunctionInstance = (MetricRedisFunction) clazz.newInstance();
            //函数表达式
            String funcStr = arg[0];


            //执行自定义函数表达式, 获取指标值
            Object metric = AviatorUtil.execute(funcStr,metricRedisFunctionInstance);
            String metricStr = metric.toString();
            //指标值和关系运算符,阈值组装得到条件关系表达式
            String exp = metricStr + arg[1];
            //Aviator解析这个关系表达式, 返回true/false
            boolean judge = (boolean) AviatorUtil.execute(exp);
            //规则命中
            if(judge) {
                //记录命中信息
                RiskInfoPO info = new RiskInfoPO();
                info.setUser_id_int(eventPO.getUser_id_int());
                //记录规则对应的策略动作
                ActionPO action = new ActionPO();
                action.setAction(rule.getAction());
                action.setInfo(info);
                //输出命中信息和策略动作
                collector.collect(action);
            }
        }
    }
}
