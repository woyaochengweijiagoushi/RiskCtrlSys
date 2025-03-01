package com.juege.RiskCtrlSys.flink.job.cep;

import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;
import java.util.Map;

/**
 * author: Imooc
 * description: Cep demo(伪代码)
 * date: 2023
 */

public class CepDemo {
    public static void main(String[] args) {
        //创建流式计算上下文环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //生成DataStream
        DataStream<String> dataStream = null;

        //生成KeyedStream
        KeyedStream<String,Tuple> keyedStream = dataStream.keyBy("");

        //生成模式(规则) ( Pattern 对象)


        /* **********************
         *
         * 个体模式
         * 1. 单例模式：只接收1个事件
         * 2. 循环模式：能接收多个事件或1个事件, 单例模式 + 量词
         * *********************/
        //生成名叫 “login” 的单个Pattern
        Pattern<String,String> pattern =
                Pattern.<String>begin("login").where(new SimpleCondition<String>() {
                    @Override
                    public boolean filter(String s) throws Exception {
                        //Patter规则内容
                        return false;
                    }
                }).times(3);


        /* **********************
         *
         * 组合模式
         *
         * 组合方式：
         * 1. next: 严格紧邻
         * 2. fallowedBy: 宽松近邻
         * 3. fallowedByAny: 非严格匹配,比 fallowedBy 更宽松
         *
         * *********************/

        //生成了两个Patten所组成的Pattern序列，分别名叫 "login", "sale"
        Pattern<String,String> patterns =
                Pattern.<String>begin("login")//.where()
                        .followedBy("sale");//.where();


        //将 Pattern 应用于 KeyedStream， 生成 PatternStream 对象

        PatternStream<String> patternStream= CEP.pattern(keyedStream,patterns);


        // 通过PatternStream 对象的 select() 方法, 将符合规则的数据提取

       DataStream patternResult = patternStream.select(new PatternSelectFunction<String, Object>() {
            /**
             * author: Imooc
             * description: TODO
             * @param map:  key: 指的是Pattern的名称。 value: 符合这个Pattern的数据
             * @return java.lang.Object
             */
            @Override
            public Object select(Map<String, List<String>> map) throws Exception {
                return null;
            }
        });

    }
}
