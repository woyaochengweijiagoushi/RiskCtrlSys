package com.juege.RiskCtrlSys.flink.job.broadcast;

import com.juege.RiskCtrlSys.model.EventPO;
import com.juege.RiskCtrlSys.model.RedisPO;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;

/**
 * author: Juege
 * description: 风控规则的 KeyedBroadcastProcessFunction
 * date: 2024
 */

public class CepKeyedBroadcastProcessFunc
        extends KeyedBroadcastProcessFunction<Integer,EventPO, RedisPO,EventPO> {

    //规则流广播状态描述器
    private final MapStateDescriptor<String, RedisPO> stateDescriptor;

    //规则组唯一编码
    String the_set_code;

    //构造函数
    public CepKeyedBroadcastProcessFunc(MapStateDescriptor<String, RedisPO> stateDescriptor, String the_set_code) {
        this.stateDescriptor = stateDescriptor;
        this.the_set_code = the_set_code;
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
            KeyedBroadcastProcessFunction<Integer, EventPO, RedisPO, EventPO>.ReadOnlyContext readOnlyContext,
            Collector<EventPO> collector) throws Exception {

        //都是只读操作
        ReadOnlyBroadcastState<String,RedisPO> broadcastState =
                readOnlyContext.getBroadcastState(stateDescriptor);


        if (broadcastState != null) {

            //判断groovy模板Hash值是否更改
            RedisPO redisPO = broadcastState.get(null);
            String hash = redisPO.getData();

        }

        //返回行为事件流
        collector.collect(eventPO);

    }

    /**
     * author: Juege
     * description: 处理广播流中的数据, 每次接收到广播流的每个记录都会调用
     * @param rulesPO:
     * @param context:
     * @param collector:
     * @return void
     */
    @Override
    public void processBroadcastElement(
            RedisPO rulesPO,
            KeyedBroadcastProcessFunction<Integer, EventPO, RedisPO, EventPO>.Context context,
            Collector<EventPO> collector) throws Exception {

        //获取广播状态
        BroadcastState<String,RedisPO> broadcastState = context.getBroadcastState(stateDescriptor);


        }

}