package com.juege.RiskCtrlSys.flink.job.window;

import com.juege.RiskCtrlSys.model.EventPO;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

/**
 * author: Juege
 * description: 指标 窗口函数 模块
 * date: 2024
 */

/* **********************
 *
 * WindowFunction<In, OUT,KEY,TIME>
 *
 * In: AggregateFunction输出结果 Tuple2<Integer, Double>
 * OUT: 输出 Tuple2<AggregateFunction输出结果, 窗口中的行为事件>
 * KEY: keyBy的key类型 (uid)
 * TIME: 窗口类型
 * *********************/

public class MetricWindowFunction implements WindowFunction<
        //In
        Tuple2<EventPO,Double>,
        //OUT
        Tuple2<EventPO,Double>,
        //KEY
        Integer,
        //TIME
        TimeWindow> {


    @Override
    public void apply(
            Integer uid,
            TimeWindow timeWindow,
            Iterable<Tuple2<EventPO,Double>> aggregation,
            Collector<Tuple2<EventPO,Double>> out
    ) throws Exception {
        Tuple2<EventPO,Double> in = aggregation.iterator().next();
//        out.collect(Tuple2.of(in.f0, in.f1));
        out.collect(in);
    }


}
