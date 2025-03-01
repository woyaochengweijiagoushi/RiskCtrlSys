package com.juege.RiskCtrlSys.flink.job.task;

import com.juege.RiskCtrlSys.flink.utils.EventConstantUtil;
import com.juege.RiskCtrlSys.model.EventPO;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * author: Juege
 * description: Flink每5分钟计算用户最近1小时的登录次数
 * date: 2024
 */


/* **********************
 *
 * 思路：
 *
 * 1。 过滤出登录的行为数据 ( filter )
 * 2.  根据用户id进行分组统计 ( keyBy )
 * 3. 设置窗口的类型,大小,步长值
 * 4. 对窗口的数据进行聚合计算
 *
 *
 * *********************/
public class LoginFreqHourTask {

    /**
     * author: Juege
     * description: 每5分钟计算用户最近1小时的登录频率的主要业务逻辑
     * @param input:  带有watermark的数据流
     * @return void
     */
    public static DataStream<Tuple2<Integer,Integer>> process(DataStream<EventPO> input) {

        /* **********************
         *
         * 窗口设计思路：
         *
         * 需求： 每5分钟统计用户最近1小时的登录频率
         *
         * 窗口的大小：1小时
         * 窗口的滑动步长：5分钟统计1次,
         *
         * [8:00 - 9:00 ) , [ 8:05 - 9:05 ) .... 登录次数
         *
         * 窗口类型： 滑动窗口
         *
         *
         *
         *
         *
         * *********************/



        //过滤出登录行为数据
        DataStream<EventPO> filterDataStream = input.filter(new FilterFunction<EventPO>() {
            @Override
            public boolean filter(EventPO eventPO) throws Exception {

                if(eventPO.getEvent_name().equals(EventConstantUtil.LOGIN_SUCCESS)) {
                    return true;
                }

                return false;
            }
        });

        // 按用户id分组
        KeyedStream<EventPO,Integer> keyedStream =  filterDataStream.keyBy(new KeySelector<EventPO, Integer>() {
            @Override
            public Integer getKey(EventPO eventPO) throws Exception {
                return eventPO.getUser_id_int();
            }
        });

        /* **********************
         *
         * 知识点：
         *
         * 1.
         * a. CountWindow: 指定数据的数量来生成窗口, 和时间无关
         * b. TimeWindow: 按照时间生成的窗口
         *
         * 滚动窗口, 滑动窗口, 会话窗口是属于 TimeWindow
         *
         * 2.
         * TumblingEventTimeWindows.of() 是生成滚动窗口
         * SlidingEventTimeWindows.of() 是生成滑动窗口
         *
         * 3.
         * * Flink 中提供了四种类型的窗口聚合函数 ( Window Function ):
         * a. ReduceFunction
         * b. AggregateFunction
         * c. ProcessWindowFunction
         * d. sum, max , min
         *
         * 4.
         *
         * Flink 四种类型的窗口聚合函数分为 2 类：
         * a. 增量聚合函数：ReduceFunction,AggregateFunction
         * b. 全量聚合函数: ProcessWindowFunction,sum,max,min
         *
         *
         * 增量聚合函数：
         * 性能高
         * 不需要缓存数据, 基于中间状态进行计算
         *
         * 全量聚合函数：
         * 性能低
         * 需要缓存数据, 基于进入窗口的全部数据进行计算
         *
         * 5.
         * 例如在对窗口数据进行排序, 取TopN
         * 需要用到 ProcessWindowFunction,
         *
         *
         * ProcessWindowFunction 结合 ReduceFunction 或者 AggregateFunction
         * 作增量计算
         *
         * *********************/

        /* **********************
         *
         *
         * 注意：
         *
         * Time 的导入包是 org.apache.flink.streaming.api.windowing.time
         *
         * *********************/


        // 窗口聚合计算

        //生成滑动窗口, 大小 (1小时) , 步长值 (5分钟)
        SingleOutputStreamOperator<Tuple2<Integer,Integer>> ac = keyedStream
                .window(SlidingEventTimeWindows.of(
                        Time.seconds(3600),
                        Time.seconds(300))
                )
                .aggregate(new LoginFreqHourAggFunction());

        //返回的是 Tuple2<用户id,最近1小时内的登录次数>
        return ac;

    }
}
