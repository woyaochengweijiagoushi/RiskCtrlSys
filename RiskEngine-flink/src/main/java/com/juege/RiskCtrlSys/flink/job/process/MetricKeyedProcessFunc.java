package com.juege.RiskCtrlSys.flink.job.process;

import com.juege.RiskCtrlSys.model.EventPO;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

/**
 * author: Juege
 * description:
 * date: 2024
 */

/* **********************
 *
 * 知识点：
 *
 * 1.
 * KeyedProcessFunction:
 * 用于处理KeyedStream的事件流
 * 相比ProcessFunction,
 * 有更多的特性, 如定时器
 *
 * 2.
 * 每个事件都会触发 processElement(),
 *
 *
 * *********************/

public class MetricKeyedProcessFunc extends KeyedProcessFunction<Integer,EventPO,String> {
    /**
     * author: Juege
     * description:
     * @param in: 输入值
     * @param ctx: 上下文
     * @param out:  输出值
     * @return void
     */
    @Override
    public void processElement(EventPO in, Context ctx, Collector<String> out) throws Exception {

        //获取当前事件流的水印
        out.collect("当前watermark：" + ctx.timerService().currentWatermark());
        out.collect("事件时间：" + ctx.timestamp());
        out.collect("------------------------------------------");
    }
}
