package com.juege.RiskCtrlSys.flink.utils;

import com.juege.RiskCtrlSys.model.EventPO;
import com.juege.RiskCtrlSys.model.EventPO2;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class FlinkPojoTest {

    @DisplayName("测试Flink pojo")
    @Test
    public void testFlinkReadKafka() throws Exception {
        System.out.println("flink pojo:"+ TypeExtractor.createTypeInfo(EventPO.class));
        System.out.println("flink pojo:"+ TypeExtractor.createTypeInfo(HashMap.class));
        System.out.println("flink pojo:"+ TypeExtractor.createTypeInfo(EventPO2.class));
    }
}
