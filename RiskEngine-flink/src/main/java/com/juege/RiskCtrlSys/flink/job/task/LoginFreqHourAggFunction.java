package com.juege.RiskCtrlSys.flink.job.task;

import com.juege.RiskCtrlSys.model.EventPO;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;

/**
 * author: Juege
 * description: 用户最近1小时的登录频率聚合计算
 * date: 2024
 */

/* **********************
 *
 * 知识点：
 *
 * 1.
 * AggregateFunction 需要指定 3 个泛型：
 * AggregateFunction<In, ACC, OUT>
 *
 * In: 输入流数据类型
 * ACC: 累加器的类别
 * OUT: 聚合结果类别
 *
 *
 * *********************/
public class LoginFreqHourAggFunction implements AggregateFunction<EventPO, Tuple2<Integer,Integer>,Tuple2<Integer,Integer>> {
    /**
     * author: Juege
     * description: 创建累加器,并赋予初始值
     * @param :
     * @return Tuple2<Integer,Integer>: 累加器数据类型Tuple2, 空间1是uid, 空间2是登录次数
     */
    @Override
    public Tuple2<Integer,Integer> createAccumulator() {
        return new Tuple2<>(0,0);
    }

    /**
     * author: Juege
     * description: 将输入数据添加到累加器, 并返回更新后的累加器
     * @param input:
     * @param acc:
     * @return com.juege.RiskCtrlSys.model.EventPO
     */
    @Override
    public Tuple2<Integer,Integer> add(EventPO input, Tuple2<Integer,Integer> acc) {

        acc.f0 = input.getUser_id_int();
        acc.f1 += 1;

        return acc;
    }

    /**
     * author: Juege
     * description: 从累加器里提取聚合结果
     * @param acc:
     * @return com.juege.RiskCtrlSys.model.EventPO
     */
    @Override
    public Tuple2<Integer,Integer> getResult(Tuple2<Integer,Integer> acc) {
        return acc;
    }

    /**
     * author: Juege
     * description: 将两个累加器合并为一个新的累加器
     * @param acc1: 累加器1
     * @param acc2: 累加器2
     * @return com.juege.RiskCtrlSys.model.EventPO
     */
    @Override
    public Tuple2<Integer,Integer>  merge(Tuple2<Integer,Integer> acc1, Tuple2<Integer,Integer> acc2) {

        //不实现

        return null;
    }
}
