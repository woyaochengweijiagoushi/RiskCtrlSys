package com.juege.RiskCtrlSys.flink.job.aggregation;

import com.juege.RiskCtrlSys.flink.job.aggregation.acc.AccAggregate;
import com.juege.RiskCtrlSys.flink.job.aggregation.acc.AccAggregateFactory;
import com.juege.RiskCtrlSys.model.EventPO;
import com.juege.RiskCtrlSys.utils.common.CommonUtil;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;

/**
 * author: Juege
 * description: 自定义求和 Sum Aggregation 算子
 * date: 2024
 */

/* **********************
 *
 * AggregateFunction<In, ACC, OUT>
 *
 * In: 输入流数据类型 EventPO
 * ACC: 累加器的类别 Tuple2<EventPO, 累加结果, 事件数量>
 * OUT: 聚合结果类别 Tuple2<EventPO, 计算结果>
 *
 *
 * *********************/

public class MetricAggFunction implements
        AggregateFunction<
                //In
                EventPO,
                //ACC
                //OUT
                Tuple3<EventPO, Double,Integer>,
                Tuple2<EventPO,Double>> {

    /* **********************
     *
     * 为什么累加数据类型设为 Double ?
     * 因为指标有些值是针对商品价格的累加,
     * 所以设为Double
     *
     *
     * *********************/


    /**
     * author: Juege
     * description: 累加器初始化
     * @param :
     * @return org.apache.flink.api.java.tuple.Tuple2<java.lang.Integer,java.lang.Double>
     */
    @Override
    public Tuple3<EventPO, Double,Integer> createAccumulator() {
        return Tuple3.of(null, 0.0,1);
    }

    /**
     * author: Juege
     * description: 输入的元素和累加器中的元素相加
     * @param eventPO:
     * @param accumulator: 累加器
     * @return java.lang.Double 累加器
     */
    @Override
    public Tuple3<EventPO, Double,Integer> add(EventPO eventPO, Tuple3<EventPO, Double,Integer> accumulator) {

        /* **********************
         *
         * 例如： 最近1小时内同一地区注册账号数量
         *       聚合计算方式是累加Sum
         *       主维度是注册行为
         *       累加器计算是直接+1
         * 例如： 用户等级为L2以上的用户浏览行为的平均停留时间
         *       聚合计算方式是平均avg
         *       主维度是事件时间
         *       累加器计算是前后行为事件时间间隔的累加
         *
         *
         * *********************/

        //获取指标计算配置中的指标主维度 比如eventtime
        String main_dim = accumulator.f0.getMetrics_conf().getMain_dim();
        //获取指标计算配置中的累加器计算方法
        String acc_aggregate = accumulator.f0.getMetrics_conf().getAggregation();


        //通过反射执行主维度字段的Getter
        //获取 EventPO 实例的主维度值
        String po_value_before =
                (String) CommonUtil.getFieldValue(eventPO,main_dim);

        String po_value_after =
                (String) CommonUtil.getFieldValue(accumulator.f0,main_dim);

        //通过反射+工厂模式执行累加器对应的计算方法
        AccAggregate acc = AccAggregateFactory.getAggregate(acc_aggregate);
        Double res = acc.aggregate(
                po_value_before,
                po_value_after);

        //累加计算
        Double sum = res + accumulator.f1;
        Integer event_count = 1 + accumulator.f2;

        return Tuple3.of(eventPO,sum,event_count);
    }

    /**
     * author: Juege
     * description: 返回结果
     * @param accumulator: 累加器
     * @return java.lang.Double
     */
    @Override
    public Tuple2<EventPO,Double> getResult(Tuple3<EventPO, Double,Integer> accumulator) {

        //获取指标计算配置中的计算方式
        String aggregate = accumulator.f0.getMetrics_conf().getFlink_filter();


        Double res = 0.0;
        if(aggregate.equals("sum")) {
            res = accumulator.f1;
        }else if(aggregate.equals("avg")) {
            res = (accumulator.f1)/accumulator.f2;
        }else {
            throw new RuntimeException();
        }

        return Tuple2.of(accumulator.f0,res);
    }

    /**
     * author: Juege
     * description:
     * @param accumulator_1: 累加器1
     * @param accumulator_2: 累加器2
     * @return java.lang.Double 累加器
     */
    @Override
    public Tuple3<EventPO, Double,Integer> merge(
            Tuple3<EventPO, Double,Integer> accumulator_1,
            Tuple3<EventPO, Double,Integer> accumulator_2) {

       return null;
    }
}
