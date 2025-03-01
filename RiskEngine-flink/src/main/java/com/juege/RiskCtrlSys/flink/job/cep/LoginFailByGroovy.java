package com.juege.RiskCtrlSys.flink.job.cep;

import com.juege.RiskCtrlSys.flink.utils.GroovyUtil;
import com.juege.RiskCtrlSys.flink.utils.KafkaUtil;
import com.juege.RiskCtrlSys.model.EventPO;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;

import java.io.IOException;

/**
 * author: Juege
 * description: 基于个体模式检测最近1分钟内登录失败超过3次的用户
 *              CEP模式：允许这3次登录失败事件之间出现其他行为事件
 *              使用 Groovy 脚本
 * date: 2024
 */

public class LoginFailByGroovy {

    Pattern<EventPO,?> pattern;


    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        // Kafka
        DataStream<EventPO> eventStream = KafkaUtil.read(args);

        //生成KeyedStream
        KeyedStream<EventPO, Integer> keyedStream = eventStream.keyBy(new KeySelector<EventPO, Integer>() {
            @Override
            public Integer getKey(EventPO eventPO) throws Exception {
                return eventPO.getUser_id_int();
            }
        });


        //解析Groovy脚本, 获取 Flink-Cep Pattern 结构体
        String clazz = "LoginFailBySingleton";
        String method = "getPattern";

        /* **********************
         *
         * 知识点：
         *
         * 1.
         * 为什么 Groovy能够动态加载脚本 ?
         * a. GroovyClassLoader打破了双亲委派模型
         * b. Groovy解析脚本, 是解析一次, 就生成新的一个类, 即使脚本内容完全一样。
         *
         * 2.
         * Groovy解析脚本会造成频繁的 full GC的解决方案
         *
         * 为什么Groovy解析脚本会引起Full GC ?
         * 答案：永久代 (Permanet Generation) 空间不足
         *
         * 为什么Groovy解析脚本生成的一次性的类不会被垃圾回收, 造成永久代 空间不足 ?
         * 答案：GC 条件不足
         *
         * Groovy解析脚本, 是解析一次, 就生成新的一个类 造成频繁的 full GC
         * 解决方案： 为每个脚本单独创建一个GroovyClassLoader对象
         *
         * *********************/

        Pattern<EventPO,?> pattern = (Pattern<EventPO,?>) GroovyUtil.groovyEval(clazz,method,null);

    }
}
