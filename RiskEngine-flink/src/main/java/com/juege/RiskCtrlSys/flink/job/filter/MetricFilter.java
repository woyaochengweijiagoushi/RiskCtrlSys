package com.juege.RiskCtrlSys.flink.job.filter;

import com.juege.RiskCtrlSys.model.EventPO;
import com.juege.RiskCtrlSys.utils.common.CommonUtil;
import org.apache.flink.api.common.functions.RichFilterFunction;

import java.util.Map;

/**
 * author: Juege
 * description: 指标计算 Filter 模块
 * date: 2024
 */

public class MetricFilter extends RichFilterFunction<EventPO> {

    /**
     * author: Juege
     * description: 过滤指标计算所需的事件行为
     * @param eventPO:
     * @return boolean
     */
    @Override
    public boolean filter(EventPO eventPO) throws Exception {

//        if(eventPO.getPartition() > 10){
//            return true;
//        }
//        return false;

        //指标计算的filter部分,有可能多个,需要切割

//        age<30&&loginCounts<5
        String filter = eventPO.getMetrics_conf().getFlink_filter();
        //返回格式 [event_name::pay,event_name::order::&&,event_name::browse::||]
        String[] filters = CommonUtil.metricFiltersplit(filter);
        //获取filter部分的字段和值
        Map<String,Map<String,String>> value = CommonUtil.getFilterKeyAndValue(filters);
        //通过反射执行对应字段的Getter,返回true or false
        return CommonUtil.getFilterValue(eventPO,value);
    }
}
