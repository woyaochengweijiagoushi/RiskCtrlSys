package com.juege.RiskCtrlSys.flink.job.cdc;

import com.alibaba.fastjson2.JSONObject;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.data.Field;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.data.Schema;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.data.Struct;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.source.SourceRecord;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import io.debezium.data.Envelope;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;


/**
 * author: Juege
 * description: 自定义Flink-CDC反序列化器 (测试)
 * date: 2024
 */

public class CustomDebeziumDeserializerTest implements DebeziumDeserializationSchema<String> {
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
            Collector<String> collector) throws Exception {

        // sourceRecord携带的数据，是Struct类型
        /* **********************
         *
         * 注意：
         * Struct对象是
         * com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.data 包
         *
         * 因为
         * Flink CDC 的 shaded 使用了 Kafka Connect 类库
         *
         * *********************/
        Struct value = (Struct)sourceRecord.value();
        // 获取变更前后的数据，名称分别为before和after
        Struct before = value.getStruct("before");
        Struct after = value.getStruct("after");

        // 定义一个json对象
        JSONObject jsonObject = new JSONObject();

        //处理after数据
        if (null != after) {
            Map<String, Object> afterMap = new HashMap<>();
            // 获取Struct中包含所有字段名，遍历即可
            Schema afterSchema = after.schema();
            for (Field field : afterSchema.fields()) {
                afterMap.put(field.name(), after.get(field));
            }
            //写入到json对象中
            jsonObject.put("after", afterMap);
        }


        //获取操作类型 READ DELETE UPDATE CREATE
        Envelope.Operation operation = Envelope.operationFor(sourceRecord);
        String type = operation.toString().toLowerCase();
        //写入到json对象中
        jsonObject.put("type", type);

        //获取 ts_ms 时间戳
        jsonObject.put("ts_ms", value.get("ts_ms"));


        //输出数据
        collector.collect(jsonObject.toString());
    }

    /**
     * author: Juege
     * description: 返回解析之后的数据类型
     * @param :
     * @return org.apache.flink.api.common.typeinfo.TypeInformation<java.lang.String>
     */
    @Override
    public TypeInformation<String> getProducedType() {
        return BasicTypeInfo.STRING_TYPE_INFO;
    }
}
