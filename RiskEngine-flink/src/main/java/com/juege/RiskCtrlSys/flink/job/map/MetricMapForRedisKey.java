package com.juege.RiskCtrlSys.flink.job.map;

import com.juege.RiskCtrlSys.flink.utils.RedisKeyUtil;
import com.juege.RiskCtrlSys.model.EventPO;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;

/**
 * author: Juege
 * description: 组装 Redis Key
 * date: 2024
 */

public class MetricMapForRedisKey implements
        MapFunction<Tuple2<EventPO,Double>,Tuple2<String,String>> {
    /**
     * author: Juege
     * description: 将输出类型转换为 Tuple2<String,String>
     * @param in:
     * @return org.apache.flink.api.java.tuple.Tuple2<java.lang.String,java.lang.String>
     */
    @Override
    public Tuple2<String, String> map(Tuple2<EventPO,Double> in) throws Exception {

        //组装Redis Key
        String key = RedisKeyUtil.redisKeyFormatForMetric(String.valueOf(in.f0));
        //组装输出格式
        Tuple2<String, String> tuple2 = new Tuple2<>();
        tuple2.f0 = key;
        tuple2.f1 = String.valueOf(in.f1);
        return tuple2;
    }
}
