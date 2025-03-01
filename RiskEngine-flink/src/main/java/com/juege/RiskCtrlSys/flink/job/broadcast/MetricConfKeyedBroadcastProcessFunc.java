package com.juege.RiskCtrlSys.flink.job.broadcast;

import com.juege.RiskCtrlSys.model.EventPO;
import com.juege.RiskCtrlSys.model.MetricsConfPO;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;

/**
 * author: Juege
 * description: 风控指标配置的 KeyedBroadcastProcessFunction
 * date: 2024
 */

public class MetricConfKeyedBroadcastProcessFunc
        extends KeyedBroadcastProcessFunction<Integer,EventPO, MetricsConfPO,EventPO> {

    //广播状态描述器
    private MapStateDescriptor<String, MetricsConfPO> stateDescriptor;

    public MetricConfKeyedBroadcastProcessFunc(MapStateDescriptor<String, MetricsConfPO> stateDescriptor) {
        this.stateDescriptor = stateDescriptor;
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
            KeyedBroadcastProcessFunction<Integer, EventPO, MetricsConfPO, EventPO>.ReadOnlyContext readOnlyContext,
            Collector<EventPO> collector) throws Exception {

        //都是只读操作
        ReadOnlyBroadcastState<String,MetricsConfPO> broadcastState =
                readOnlyContext.getBroadcastState(stateDescriptor);

        if (broadcastState != null) {
            //取出广播状态的 指标配置POJO
            MetricsConfPO metricsConfPO = broadcastState.get(null);

            //根据指标配置的行为类型 和 事件流的行为类型 匹配
//            if (metricsConfPO.getEvent().equals(eventPO.getEvent_type())) {
//
//                // 获取事件PO中的Metrics_conf列表
//                List<MetricsConfPO> metricsConfPOList = eventPO.getMetrics_conf();
//
//                // 如果Metrics_conf列表为空，则创建一个新的空列表
//                if (metricsConfPOList == null) {
//                    metricsConfPOList = new java.util.ArrayList<>();
//                }
//
//                // 将Metrics_confPO添加到Metrics_conf列表中
//                metricsConfPOList.add(metricsConfPO);
//
//                // 设置事件PO中的Metrics_conf列表为更新后的列表
//                eventPO.setMetrics_conf(metricsConfPOList);
//
//            }

        }

        //返回行为事件流
        collector.collect(eventPO);

    }

    /**
     * author: Juege
     * description: 处理广播流中的数据, 每次接收到广播流的每个记录都会调用
     * @param metricsConfPO:
     * @param context:
     * @param collector:
     * @return void
     */
    @Override
    public void processBroadcastElement(
            MetricsConfPO metricsConfPO,
            KeyedBroadcastProcessFunction<Integer, EventPO, MetricsConfPO, EventPO>.Context context,
            Collector<EventPO> collector) throws Exception {

        //获取广播状态
        BroadcastState<String,MetricsConfPO> broadcastState =
                context.getBroadcastState(stateDescriptor);

        //清空历史广播状态数据
        broadcastState.clear();

        //将最新的广播流数据(指标配置)放到广播状态
        broadcastState.put(null, metricsConfPO);

    }
}
