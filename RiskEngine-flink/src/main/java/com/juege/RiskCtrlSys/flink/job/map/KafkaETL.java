package com.juege.RiskCtrlSys.flink.job.map;

import com.juege.RiskCtrlSys.model.EventPO;
import com.juege.RiskCtrlSys.model.KafkaMessagePO;
import org.apache.flink.api.common.functions.MapFunction;

/**
 * author: Juege
 * description: 对Kafka消息ETL处理
 * date: 2024
 */

public class KafkaETL implements MapFunction<KafkaMessagePO, EventPO> {


    /**
     * author: Juege
     * description: 从Kafka Json消息中提取项目需要的字段信息, 即 EventPO 对象
     * @param kafkaMessagePO:
     * @return com.juege.RiskCtrlSys.model.EventPO
     */
    @Override
    public EventPO map(KafkaMessagePO kafkaMessagePO) throws Exception {
        EventPO eventPo = new EventPO(
                kafkaMessagePO.getUser_id_int(),
                kafkaMessagePO.getEvent_time(),
                kafkaMessagePO.getEvent_target_name(),
                kafkaMessagePO.getEvent_name(),
                kafkaMessagePO.getEvent_type(),
                kafkaMessagePO.getEvent_context()
        );
        // 携带上Kafka消息的offset和partition信息
        eventPo.setOffset(kafkaMessagePO.getOffset());
        eventPo.setPartition(kafkaMessagePO.getPartition());
        return eventPo;
    }
}
