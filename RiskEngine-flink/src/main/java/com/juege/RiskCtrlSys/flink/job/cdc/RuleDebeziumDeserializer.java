package com.juege.RiskCtrlSys.flink.job.cdc;

import com.juege.RiskCtrlSys.flink.utils.FlinkCDCUtil;
import com.juege.RiskCtrlSys.model.SingleRulePO;
import com.juege.RiskCtrlSys.utils.common.CommonUtil;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.data.Struct;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.source.SourceRecord;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;

import java.util.Map;


/**
 * author: Juege
 * description: 自定义Flink-CDC反序列化器 (原子规则表)
 * date: 2024
 */

public class RuleDebeziumDeserializer implements DebeziumDeserializationSchema<SingleRulePO> {
    /**
     * author: Juege
     * description: 反序列化的业务逻辑
     * @param sourceRecord: Debezium 结果集
     * @param collector: 数据输出
     * @return void
     */
    @Override
    public void deserialize(
            SourceRecord sourceRecord,
            Collector<SingleRulePO> collector) throws Exception {

        Struct value = (Struct)sourceRecord.value();
        //自定义反序列化
        Map<String,String> map = FlinkCDCUtil.deserialize(value);
        //通过反射给对象属性赋值
        SingleRulePO rulePO = CommonUtil.setObjFieldsValue(SingleRulePO.class,map);
        //获取ts_ms
        Long ts_ms = Long.parseLong(map.get("ts_ms"));
        rulePO.setTs_ms(ts_ms);

        //输出数据
        collector.collect(rulePO);
    }

    /**
     * author: Juege
     * description: 返回解析之后的数据类型
     * @param :
     * @return org.apache.flink.api.common.typeinfo.TypeInformation<java.lang.String>
     */
    @Override
    public TypeInformation<SingleRulePO> getProducedType() {
          return TypeInformation.of(SingleRulePO.class);
    }
}
