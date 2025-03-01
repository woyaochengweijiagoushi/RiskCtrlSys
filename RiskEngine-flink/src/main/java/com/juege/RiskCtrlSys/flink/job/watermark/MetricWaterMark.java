package com.juege.RiskCtrlSys.flink.job.watermark;

import com.juege.RiskCtrlSys.model.EventPO;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkOutput;

/**
 * author: Juege
 * description: 自定义水印生成器
 * date: 2024
 */

public class MetricWaterMark implements WatermarkGenerator<EventPO> {

    //数据延迟的最大时间
    private long maxOutOfOrderness;

    public MetricWaterMark(long maxOutOfOrderness) {
        this.maxOutOfOrderness = maxOutOfOrderness;
    }

    /**
     * author: Juege
     * description: 每个事件都会调用这个方法,生成水印,然后发到下游
     * @param eventPO: 当前事件
     * @param l: 时间戳
     * @param watermarkOutput: 包含水印的WatermarkOutput
     * @return void
     */
    @Override
    public void onEvent(EventPO eventPO, long l, WatermarkOutput watermarkOutput) {

        //不实现
    }

    /**
     * author: Juege
     * description: 周期性生成水印
     * @param watermarkOutput:
     * @return void 
     */
    @Override
    public void onPeriodicEmit(WatermarkOutput watermarkOutput) {

        /* **********************
         *
         * 知识点：
         *
         * 1.
         *
         * 如果数据量比较大的时候，每条数据都生成一个水印的话，会影响性能,
         * 所以需要周期性的生成水印
         *
         * 2.
         *
         * 水印生成周期可以这样设置
         * env.getConfig().setAutoWatermarkInterval(5000L);
         * 若 ProcessingTime 时间语义, 必须要设置
         * 若 EventTime 时间语义, setAutoWatermarkInterval() 是有默认值的
         *
         * setAutoWatermarkInterval(5000L)是
         * 每隔 5毫秒 自动向事件流里注入一个水位线
         *
         *
         * 水印生成周期也能在这个方法里自定义实现
         *
         * *********************/

        //不实现

    }
}
