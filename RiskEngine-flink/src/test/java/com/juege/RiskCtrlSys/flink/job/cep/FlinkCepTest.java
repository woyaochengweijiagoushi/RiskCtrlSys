package com.juege.RiskCtrlSys.flink.job.cep;

import com.juege.RiskCtrlSys.flink.model.CepWarnTestPO;
import com.juege.RiskCtrlSys.flink.model.SimpleEventTestPO;
import com.juege.RiskCtrlSys.flink.utils.GroovyUtil;
import com.juege.RiskCtrlSys.utils.date.DateUtil;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.functions.PatternProcessFunction;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * author: Juege
 * description: Flink-Cep 单元测试
 * date: 2024
 */

public class FlinkCepTest {

    /**
     * author: Juege
     * description: 公共方法: 水印和分组
     * @param env:
     * @param dataStream:
     * @return org.apache.flink.streaming.api.datastream.KeyedStream<com.juege.RiskCtrlSys.flink.model.SimpleEventTestPO,java.lang.Integer>
     */
    private KeyedStream<SimpleEventTestPO, Integer> getStream(
            StreamExecutionEnvironment env,
            DataStream<SimpleEventTestPO> dataStream) {

        return dataStream
                //必须生成水印
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                        //最大的数据延迟时间
                        .<SimpleEventTestPO>forBoundedOutOfOrderness(Duration.ZERO)
                        //事件流的每个事件元素设置时间戳
                        .withTimestampAssigner(new SerializableTimestampAssigner<SimpleEventTestPO>() {
                            @Override
                            public long extractTimestamp(SimpleEventTestPO event, long l) {
                                return event.getEvent_time();
                            }
                        }))
                //分组
                //双冒号是JDK8 Lambda的简化写法
                .keyBy(SimpleEventTestPO::getUser_id_int);

    }


    @DisplayName("测试基于Flink-Cep循环模式检测登录失败超过3次的用户,event_time是long类型")
    @Test
    void testLoginFailByCep() throws Exception {

        //流式计算上下文环境
        StreamExecutionEnvironment env=StreamExecutionEnvironment.getExecutionEnvironment();
        //方便测试 并行度设置为1
        env.setParallelism(1);
        //生成事件流
        DataStream<SimpleEventTestPO> dataStream = env.fromElements(
                new SimpleEventTestPO(1,"fail",2000L),
                new SimpleEventTestPO(1,"success",3000L),
                new SimpleEventTestPO(1,"fail",3000L),
                new SimpleEventTestPO(2,"fail",4000L),
                new SimpleEventTestPO(1,"fail",5000L),
                new SimpleEventTestPO(2,"fail",6000L),
                new SimpleEventTestPO(2,"fail",7000L),
                new SimpleEventTestPO(3,"fail",8000L)
        );
        //根据uid进行分组
        KeyedStream<SimpleEventTestPO, Integer> stream = getStream(env,dataStream);

        //定义 Pattern
        Pattern<SimpleEventTestPO, SimpleEventTestPO> pattern = Pattern
                .<SimpleEventTestPO>begin("cep-login-fail")
                .where(new SimpleCondition<SimpleEventTestPO>() {
                    @Override
                    public boolean filter(SimpleEventTestPO value) throws Exception {
                        return value.getEvent_name().equals("fail");
                    }
                })
                .times(3);

        //将Pattern应用到事件流
        PatternStream<SimpleEventTestPO> patternStream = CEP.pattern(stream, pattern);

        //提取匹配事件
        patternStream.process(new PatternProcessFunction<SimpleEventTestPO, String>() {
            @Override
            public void processMatch(Map<String, List<SimpleEventTestPO>> map, Context context, Collector<String> collector) throws Exception {
                //提取三次登录失败事件
                List<SimpleEventTestPO> list = map.get("cep-login-fail");
                SimpleEventTestPO first =list.get(0);
                SimpleEventTestPO second =list.get(1);
                SimpleEventTestPO third =list.get(2);
                StringBuilder builder = new StringBuilder();
                builder
                        .append("uid:")
                        .append(first.getUser_id_int())
                        .append(", 登录失败时间:")
                        .append(first.getEvent_time())
                        .append(",")
                        .append(second.getEvent_time())
                        .append(",")
                        .append(third.getEvent_time());
                collector.collect(builder.toString());
            }
        })
        .print("warning");

        env.execute();

    }

    @DisplayName("测试基于Flink-Cep循环模式检测连续事件的用户")
    @Test
    void testLoginFailConsecutiveByCep() throws Exception {

        //流式计算上下文环境
        StreamExecutionEnvironment env=StreamExecutionEnvironment.getExecutionEnvironment();
        //方便测试 并行度设置为1
        env.setParallelism(1);
        //生成事件流
        DataStream<SimpleEventTestPO> dataStream = env.fromElements(
                new SimpleEventTestPO(1,"fail",2000L),
                new SimpleEventTestPO(1,"register",3000L),
                new SimpleEventTestPO(1,"fail",4000L),
                new SimpleEventTestPO(1,"success",5000L),
                new SimpleEventTestPO(2,"fail",6000L),
                new SimpleEventTestPO(2,"fail",7000L),
                new SimpleEventTestPO(3,"success",8000L)
        );
        //根据uid进行分组
        KeyedStream<SimpleEventTestPO, Integer> stream = getStream(env,dataStream);

        //定义 Pattern
        Pattern<SimpleEventTestPO, SimpleEventTestPO> pattern =
                Pattern
                        .<SimpleEventTestPO>begin("cep-login-fail-consecutive")
                        .where(new SimpleCondition<SimpleEventTestPO>() {
                            @Override
                            public boolean filter(SimpleEventTestPO value) throws Exception {
                                return value.getEvent_name().equals("fail");
                            }
                        })
                        //times是等于阈值
                        .times(2)
                        //严格紧邻(连续事件)
                        .consecutive();



        //将Pattern应用到事件流
        PatternStream<SimpleEventTestPO> patternStream = CEP.pattern(stream, pattern);

        //提取匹配事件
        patternStream.process(new PatternProcessFunction<SimpleEventTestPO, String>() {
                    @Override
                    public void processMatch(Map<String, List<SimpleEventTestPO>> map, Context context, Collector<String> collector) throws Exception {
                        //提取事件
                        List<SimpleEventTestPO> event = map.get("cep-login-fail-consecutive");
                        CepWarnTestPO warn = new CepWarnTestPO(event,"连续登录失败");
                        collector.collect(warn.toString() );
                    }
                })
                .print("warning");

        env.execute();

    }


    @DisplayName("测试基于Flink-Cep组合模式检测连续事件的用户")
    @Test
    void testLoginFailConsecutiveWithCompositeByCep() throws Exception {

        //流式计算上下文环境
        StreamExecutionEnvironment env=StreamExecutionEnvironment.getExecutionEnvironment();
        //方便测试 并行度设置为1
        env.setParallelism(1);
        //生成事件流
        DataStream<SimpleEventTestPO> dataStream = env.fromElements(
                new SimpleEventTestPO(1,"fail",2000L),
                new SimpleEventTestPO(1,"register",3000L),
                new SimpleEventTestPO(1,"success",7000L),
                new SimpleEventTestPO(2,"fail",4000L),
                new SimpleEventTestPO(2,"fail",19000L),
                new SimpleEventTestPO(2,"success",7000L),
                new SimpleEventTestPO(2,"fail",8000L),
                new SimpleEventTestPO(3,"success",9000L)
        );
        //根据uid进行分组
        KeyedStream<SimpleEventTestPO, Integer> stream = getStream(env,dataStream);

        //定义 Pattern
        Pattern<SimpleEventTestPO, SimpleEventTestPO> pattern =
                Pattern
                .<SimpleEventTestPO>begin("cep-login-first")
                .where(new SimpleCondition<SimpleEventTestPO>() {
                    @Override
                    public boolean filter(SimpleEventTestPO value) throws Exception {
                        return value.getEvent_name().equals("fail");
                    }
                })
                //严格紧邻 (连续事件)
//                .next("cep-login-second")
                //松散紧邻 (非连续事件)
                .followedBy("cep-login-second")
                .where(new SimpleCondition<SimpleEventTestPO>() {
                    @Override
                    public boolean filter(SimpleEventTestPO value) throws Exception {
                        return value.getEvent_name().equals("success");
                    }
                })
                .within(Time.seconds(6));



        //将Pattern应用到事件流
        PatternStream<SimpleEventTestPO> patternStream = CEP.pattern(stream, pattern);

        //提取匹配事件
        patternStream.process(new PatternProcessFunction<SimpleEventTestPO, String>() {
                    @Override
                    public void processMatch(Map<String, List<SimpleEventTestPO>> map, Context context, Collector<String> collector) throws Exception {
                        //提取事件
                        List<SimpleEventTestPO> first = map.get("cep-login-first");
                        List<SimpleEventTestPO> second = map.get("cep-login-second");
                        CepWarnTestPO warn1 = new CepWarnTestPO(first,"登录失败");
                        CepWarnTestPO warn2 = new CepWarnTestPO(second,"登录成功");
                        collector.collect(warn1.toString() );
                        collector.collect(warn2.toString());
                    }
                })
                .print("warning");

        env.execute();

    }


    @DisplayName("测试使用Groovy脚本加载Pattern")
    @Test
    void testLoginFailByGroovy() throws Exception {

        //流式计算上下文环境
        StreamExecutionEnvironment env=StreamExecutionEnvironment.getExecutionEnvironment();
        //方便测试 并行度设置为1
        env.setParallelism(1);

        //生成事件流
        DataStream<SimpleEventTestPO> dataStream = env.fromElements(
                        new SimpleEventTestPO(1,"fail",2000L),
                        new SimpleEventTestPO(1,"fail",3000L),
                        new SimpleEventTestPO(2,"fail",4000L),
                        new SimpleEventTestPO(1,"fail",5000L),
                        new SimpleEventTestPO(2,"fail",7000L),
                        new SimpleEventTestPO(2,"fail",8000L),
                        new SimpleEventTestPO(2,"success",6000L)
                );
        //根据uid进行分组
        KeyedStream<SimpleEventTestPO, Integer> stream = getStream(env,dataStream);

        //Groovy脚本名称
        String loginFailBySingleton = "LoginFailBySingleton";
        String clazz = loginFailBySingleton;
        //反射执行的脚本方法
        String method = "getPattern";

        Pattern<SimpleEventTestPO,SimpleEventTestPO> pattern =
                (Pattern<SimpleEventTestPO,SimpleEventTestPO>) GroovyUtil.groovyEval(clazz,method,null);
        PatternStream<SimpleEventTestPO> patternStream = CEP.pattern(stream, pattern);

        //匹配事件提取
        patternStream.process(
                new PatternProcessFunction<SimpleEventTestPO, String>() {
                    @Override
                    public void processMatch(Map<String, List<SimpleEventTestPO>> map, Context context, Collector<String> collector) throws Exception {
                        List<SimpleEventTestPO> event = map.get("login_fail_first");
                        CepWarnTestPO warn = new CepWarnTestPO(event,"从Groovy加载Pattern");
                        collector.collect(warn.toString() );
                    }
        })
        .print("warning");

        env.execute();

    }

    @DisplayName("测试基于Flink-Cep循环模式检测登录失败超过3次的用户,event_time是String类型")
    @Test
    void testLoginFailByCepWithTimeString() throws Exception {

        //流式计算上下文环境
        StreamExecutionEnvironment env=StreamExecutionEnvironment.getExecutionEnvironment();
        //方便测试 并行度设置为1
        env.setParallelism(1);
        //生成事件流
        DataStream<SimpleEventTestPO> dataStream = env.fromElements(
                new SimpleEventTestPO(1,"fail","2024-01-01 00:03:00"),
                new SimpleEventTestPO(1,"fail","2024-01-01 00:03:01"),
                new SimpleEventTestPO(2,"fail","2024-01-01 00:03:02"),
                new SimpleEventTestPO(1,"fail","2024-01-01 00:03:03"),
                new SimpleEventTestPO(2,"fail","2024-01-01 00:03:04"),
                new SimpleEventTestPO(2,"fail","2024-01-01 00:03:05"),
                new SimpleEventTestPO(3,"fail","2024-01-01 00:03:06")
        );
        //根据uid进行分组
        KeyedStream<SimpleEventTestPO, Integer> stream =
                dataStream
                        //必须生成水印
                        .assignTimestampsAndWatermarks(
                                WatermarkStrategy
                                        //最大的数据延迟时间
                                        .<SimpleEventTestPO>forBoundedOutOfOrderness(Duration.ZERO)
                                        //事件流的每个事件元素设置时间戳
                                        .withTimestampAssigner(new SerializableTimestampAssigner<SimpleEventTestPO>() {
                                            @Override
                                            public long extractTimestamp(SimpleEventTestPO event, long l) {
                                                //提取时间字段, 并转换时间戳，时间戳是毫秒。
                                                LocalDateTime localDateTime = DateUtil.convertStr2LocalDateTime(event.getEvent_time_string());
                                                return DateUtil.convertLocalDateTime2Timestamp(localDateTime);
                                            }
                                        }))
                        //分组
                        //双冒号是JDK8 Lambda的简化写法
                        .keyBy(SimpleEventTestPO::getUser_id_int);

        //定义 Pattern
        Pattern<SimpleEventTestPO, SimpleEventTestPO> pattern = Pattern.<SimpleEventTestPO>begin("cep-login-fail")
                .where(new SimpleCondition<SimpleEventTestPO>() {
                    @Override
                    public boolean filter(SimpleEventTestPO value) throws Exception {
                        return value.getEvent_name().equals("fail");
                    }
                }).times(3);

        //将Pattern应用到事件流
        PatternStream<SimpleEventTestPO> patternStream = CEP.pattern(stream, pattern);

        //提取匹配事件
        patternStream.process(new PatternProcessFunction<SimpleEventTestPO, String>() {
                    @Override
                    public void processMatch(Map<String, List<SimpleEventTestPO>> map, Context context, Collector<String> collector) throws Exception {
                        //提取三次登录失败事件
                        List<SimpleEventTestPO> list = map.get("cep-login-fail");
                        SimpleEventTestPO first =list.get(0);
                        SimpleEventTestPO second =list.get(1);
                        SimpleEventTestPO third =list.get(2);
                        StringBuilder builder = new StringBuilder();
                        builder
                                .append("uid:")
                                .append(first.getUser_id_int())
                                .append(", 登录失败时间:")
                                .append(first.getEvent_time_string())
                                .append(",")
                                .append(second.getEvent_time_string())
                                .append(",")
                                .append(third.getEvent_time_string());
                        collector.collect(builder.toString());
                    }
                })
                .print("warning");

        env.execute();

    }

    public static void main(String[] args) {
        new Thread();
    }

}
