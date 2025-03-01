package com.juege.RiskCtrlSys.flink.job.keyby;

import com.juege.RiskCtrlSys.model.EventPO;
import com.juege.RiskCtrlSys.utils.common.CommonUtil;
import org.apache.flink.api.java.functions.KeySelector;

/**
 * author: Juege
 * description: 指标 KeyBy 通用模块
 * date: 2024
 */

public class  MetricKeyBy implements KeySelector<EventPO,Integer> {

    @Override
    public Integer getKey(EventPO eventPO) throws Exception {

        /* **********************
         *
         * 项目的 Keyby 的 key 只有1个,而且限定是 数值型的 uid,
         * 如果 keyby 需要有多个, 怎办?
         *
         * 答案：
         * KeySelector 的 key 的数据类型设置为 Tuple2<k1,k2> 或 Tuple3<k1,k2,k3>,
         * 这样就可以实现 多个 key 值的组合的分组,
         *
         *
         * *********************/

        //指标计算的 keyBy 部分
        String keyByStr = eventPO.getMetrics_conf().getFlink_keyby();

        /* **********************
         *
         * 思路：
         *
         * 指标配置参数,获取到的只是属性名,
         * 需要执行属性名所对应的 EventPO对象 的Getter方法
         *
         * *********************/

        //从字符串获取到的方法名, 通过反射执行
        String uid = (String)CommonUtil.getFieldValue(eventPO,keyByStr);

        //String 转 Integer
        return Integer.parseInt(uid);
    }
}
