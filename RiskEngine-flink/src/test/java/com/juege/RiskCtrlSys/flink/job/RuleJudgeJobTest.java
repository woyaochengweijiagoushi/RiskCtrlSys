package com.juege.RiskCtrlSys.flink.job;

import com.juege.RiskCtrlSys.commons.constants.ConstantsUtil;
import com.juege.RiskCtrlSys.flink.job.cdc.RuleDebeziumDeserializer;
import com.juege.RiskCtrlSys.flink.job.cdc.RulesDebeziumDeserializer;
import com.juege.RiskCtrlSys.flink.job.join.RulesProcessJoinFunction;
import com.juege.RiskCtrlSys.flink.job.map.RulesFlatMap;
import com.juege.RiskCtrlSys.flink.job.watermark.RulesSerializableTimestampAssigner;
import com.juege.RiskCtrlSys.flink.job.watermark.SingleRuleSerializableTimestampAssigner;
import com.juege.RiskCtrlSys.flink.utils.*;
import com.juege.RiskCtrlSys.model.EventPO;
import com.juege.RiskCtrlSys.model.RulesPO;
import com.juege.RiskCtrlSys.model.SingleRulePO;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Properties;

/**
 * author: Juege
 * description: 风控规则判断Job 单元测试
 * date: 2024
 */

public class RuleJudgeJobTest {

    @DisplayName("测试唯一编码为RULES_2的规则组进行风控规则判断")
    @Test
    public void testRuleJudge() throws Exception {

        ParameterTool tool = ParameterUtil.getParameters();
        //获取任务提交参数传入的规则组唯一编码
        String set_code = "RULES_2";
        Properties p = new Properties();

        /* **********************
         *
         * 消费Kafka, 生成行为事件流,并按照用户ID分组
         *
         * *********************/
        DataStream<EventPO> eventStream = KafkaUtil.read(tool);
        KeyedStream<EventPO, Integer> keyedStream = eventStream.keyBy(EventPO::getUser_id_int);
        StreamExecutionEnvironment env = KafkaUtil.env;
        //打印
        //keyedStream.print();
        env.setParallelism(1);

        /* **********************
         *
         * Flink-CDC 监听原子规则表
         *
         * *********************/
        String ruleTableN = ConstantsUtil.TABLE_NAME_RULE;
        String ruleSourceN = "juege-cdc-rule";
        DataStream<SingleRulePO> ruleStream = DataStreamUtil.buildMysqlCDCStream(env,tool,p,ruleTableN,new RuleDebeziumDeserializer(),ruleSourceN,"1",new SingleRuleSerializableTimestampAssigner());
        //打印
        //ruleStream.print();

        /* **********************
         *
         * Flink-CDC 监听规则组表
         *
         * *********************/
        String rulesTableN = ConstantsUtil.TABLE_NAME_RULE_SET;
        String rulesSourceN = "juege-cdc-rules";
        DataStream<RulesPO> rulesStream = DataStreamUtil.buildMysqlCDCStream(env,tool,p,rulesTableN,new RulesDebeziumDeserializer(),rulesSourceN,"2",new RulesSerializableTimestampAssigner());
        //打印
        //rulesStream.print();



        /* **********************
         *
         * 原子规则 和 规则组 双流Join
         *
         * *********************/
        //以规则唯一编码 (rule_code) 作为Join的key
        KeyedStream<SingleRulePO, String> ruleKeyedStream = ruleStream.keyBy(SingleRulePO::getRule_code);
        KeyedStream<RulesPO, String> rulesKeyedStream = rulesStream.keyBy(RulesPO::getRule_code);
        DataStream<RulesPO> joinStream = JoinUtil.intervalJoinStream(
                rulesKeyedStream,
                ruleKeyedStream,
                -5,5,
                new RulesProcessJoinFunction());
        //打印
        //joinStream.print();

        /* **********************
         *
         * 规则广播流合并行为事件流,并将规则组写入行为事件
         *
         * *********************/
        //转为广播流, 将规则组写入广播状态, 并和行为事件流合并,
        SingleOutputStreamOperator<EventPO> theRulesStream = RuleUtil.doRuleBroadcastStream(env,tool,joinStream,keyedStream,set_code);
        //将指定的规则组写入行为事件
        SingleOutputStreamOperator<EventPO> eventRuleStream = theRulesStream.flatMap(new RulesFlatMap());
       //打印
        eventRuleStream.print();


        env.execute();

    }
}
