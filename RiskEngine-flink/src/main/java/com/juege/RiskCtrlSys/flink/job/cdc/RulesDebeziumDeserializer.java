package com.juege.RiskCtrlSys.flink.job.cdc;

import com.juege.RiskCtrlSys.flink.utils.FlinkCDCUtil;
import com.juege.RiskCtrlSys.model.RulesPO;
import com.juege.RiskCtrlSys.utils.common.CommonUtil;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.data.Struct;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.source.SourceRecord;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;

import java.util.Map;


/**
 * author: Juege
 * description: 自定义Flink-CDC反序列化器 (规则组表)
 * date: 2024
 */

public class RulesDebeziumDeserializer implements DebeziumDeserializationSchema<RulesPO> {
    /**
     * author: Juege
     * description: 反序列化的业务逻辑
     * @param sourceRecord: cdc捕获的原始数据
     * @param collector: 数据输出
     * @return void
     */
    @Override
    public void deserialize(
            SourceRecord sourceRecord,
            Collector<RulesPO> collector) throws Exception {


        Struct value = (Struct)sourceRecord.value();
        Map<String,String> map = FlinkCDCUtil.deserialize(value);
        //通过反射给对象属性赋值
        RulesPO rulesPO = CommonUtil.setObjFieldsValue(RulesPO.class,map);

        //获取ts_ms
        Long ts_ms = Long.parseLong(map.get("ts_ms"));
        rulesPO.setTs_ms(ts_ms);

        //输出数据
        collector.collect(rulesPO);
    }

    /**
     * author: Juege
     * description: 返回解析之后的数据类型
     * @param :
     * @return org.apache.flink.api.common.typeinfo.TypeInformation<java.lang.String>
     */
    @Override
    public TypeInformation<RulesPO> getProducedType() {
          return TypeInformation.of(RulesPO.class);
    }
}
