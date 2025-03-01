package com.juege.RiskCtrlSys.flink.job.task;

import com.juege.RiskCtrlSys.flink.utils.KafkaUtil;
import com.juege.RiskCtrlSys.flink.utils.ParameterUtil;
import com.juege.RiskCtrlSys.model.EventPO;
import com.juege.RiskCtrlSys.utils.date.DateUtil;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * author: Juege
 * description: Flink消费Kafka数据并进行聚合计算
 * date: 2024
 */

/* **********************
 *
 * Flink消费Kafka数据并进行聚合计算 的思路：
 *
 * 1. Flink消费Kafka
 * 2. 将 kafka Json的数据 转换 POJO对象
 * 3. 将 POJO对象 的时间戳 ( event_time )
 * 4. 生成水印 ( watermark )
 * 5. 通过 keyBy() 对事件数据进行分流
 * 6. 通过窗口函数进行聚合计算
 * 7. 通过State保存中间结果
 *
 * *********************/


public class MetricWithKafka {

    public static void main(String[] args) throws Exception {


        //流计算上下文环境
        StreamExecutionEnvironment env = KafkaUtil.env;

        //========== State 和 CheckPoint 设置 ======= //

        //1分钟 (毫秒) = 1* 60 * 1000 = 60000 (毫秒)

        //多久执行一次Checkpoint
        env.enableCheckpointing(60000);
        //每次Checkpoint要在 1分钟内完成，否则就丢弃
        env.getCheckpointConfig().setCheckpointTimeout(60000);
        //Checkpoint的语义 设置为 精确一次 ( EXACTLY_ONCE )
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //Checkpoint允许失败的次数
        env.getCheckpointConfig().setTolerableCheckpointFailureNumber(3);
        //同一时间只有1个Checkpoint在运行
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);

        /* **********************
         *
         * 注意：
         *
         * Flink 1.12 之前，设置流计算的时间类型是通过以下方法
         * setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
         *
         * Flink 1.12 之后
         * setStreamTimeCharacteristic() 被抛弃
         * 流计算的时间类型默认为事件时间类型 ( EventTime )
         *
         * *********************/


        /* **********************
         *
         * 知识点：
         *
         * 1：
         *
         * Checkpoint机制开启之后，State会随着Checkpoint作持久化的存储。
         * State的存储地方是通过 StateBackend 决定
         *
         * 2：
         * StateBackend 定义了状态是如何存储的
         * MemoryStateBackend 会将工作状态（Task State）存储在 TaskManager 的内存中，将检查点（Job State）存储在 JobManager 的内存中
         * FsStateBackend 会将工作状态（Task State）存储在 TaskManager 的内存中，将检查点（Job State）存储在文件系统中（通常是分布式文件系统）
         * RocksDBStateBackend 会把工作状态（Task State）存储在 RocksDB 中，将检查点存储在文件系统中（类似 FsStateBackend）
         *
         * 3.
         * Flink 内置了以下这些开箱即用的 state backends：
         * HashMapStateBackend (默认) ( MemoryStateBackend,FsStateBackend的实现 )
         * EmbeddedRocksDBStateBackend ( RocksDBStateBackend的实现 )
         *
         * *********************/

        //State的设置
        env.setStateBackend(new HashMapStateBackend());

        /* **********************
         *
         * 知识点：
         *
         * 4：
         * Flink 支持不同级别的并行度设置：
         * a 配置文件级别：flink-conf.yaml
         * b env 级别
         * c 算子级别：每个算子都可以单独设置并行度, 1个job可以包含不同的并行度
         * d Client 级别：任务提交的命令行进行设置
         *
         * 5.
         * 不同级别设置的并行度的优先级
         * 算子级别 > env级别 > Client级别 > 配置文件级别
         *
         * 6. 并行度的数值是不是越大越好 ？
         * 并行度的数值不是越大越好，要合理。
         *
         * 6.1
         * Job会划分为多个Task
         * 这些Task会被分配到TaskManager运行
         * TaskManager运行Task是每个Task，单独一个线程。
         * 会造成线程切换的开销, 引起效率的低下。
         *
         *
         * 6.2
         * 并行度和TaskSlot有关, TaskSlot是逻辑概念, 并不是真正的物理存在
         * 假设一台TaskManager设置了3个TaskSlot
         * 每个TaskSlot在同一时间可以处理一个Task，
         * 一台TaskManager在同一时间可以处理3个Task.
         * TaskSlot对不同task的处理提供了资源隔离( 内存的隔离 )
         *
         * 6.3
         * TaskSlot数量在哪里设置？
         * a. flink-conf.yaml
         * b. 提交任务的命令行进行设置
         *
         * 6.4
         * TaskManager的TaskSlot的数量决定了TaskManager的并行度
         *
         * 假如有3台TaskManager，
         * 每台的TaskManager设置3个TaskSlot。
         * 3台TaskManager一共有 3*3=9 个TaskSlot，
         * 3台TaskManager在同一时间可以处理 9 个Task,
         * 如果并行度设置为1，
         * 只用了一个TaskSlot，有8个TaskSlot空闲。
         *
         *
         *
         * *********************/

        //并行度的设置
        // 3台TaskManager，每台的TaskManager设置3个TaskSlot
        // 合理的并行度应该是 6~9
        env.setParallelism(6);

        //ParameterTool 注册为 global
        ParameterTool parameterTool = ParameterUtil.getParameters(args);
        env.getConfig().setGlobalJobParameters(parameterTool);


        //加载Kafka数据源
        DataStream<EventPO> eventStream =  KafkaUtil.read(parameterTool);


        /* **********************
         *
         * 知识点：
         *
         * 7：
         *
         * 使用的流计算的时间类型是 事件时间类型(EventTime).
         * Flink必须要知道事件的时间戳字段，
         * 这就要求事件流必须带有时间戳字段。
         * Flink 通过TimestampAssigner API来提取事件流携带的时间字段
         *
         *
         * *********************/

        //============ 提取 EventPO 对象的时间字段 ======= //
        SerializableTimestampAssigner<EventPO> serializableTimestampAssigner = new SerializableTimestampAssigner<EventPO>() {

            @Override
            public long extractTimestamp(EventPO eventPO, long l) {

                //提取时间字段, 并转换时间戳，时间戳是毫秒。
                LocalDateTime localDateTime = DateUtil.convertStr2LocalDateTime(eventPO.getEvent_time());
                return DateUtil.convertLocalDateTime2Timestamp(localDateTime);
            }
        };


        //============ 生成水印 ( watermark: 允许数据延迟的时间 ) ======= //

        //数据延迟的最大时间10秒 ( 要转换为毫秒 )
        long maxOutOfOrderness = 10 * 1000L;

        /* **********************
         * 注意：
         *
         * Flink 1.12之后建议使用
         * assignTimestampsAndWatermarks(WatermarkStrategy)的方式生成watermark,
         *
         * WatermarkStrategy需要含有
         * TimestampAssigner 和 WatermarkGenerator
         *
         *
         * *********************/


        //assignTimestampsAndWatermarks(WatermarkStrategy)的方式生成watermark
        DataStream<EventPO> eventPODataStreamWithWaterMark = eventStream.assignTimestampsAndWatermarks(

                WatermarkStrategy
                        /* **********************
                         *
                         * 知识点：
                         *
                         * 8：
                         * watermark策略:
                         *
                         * 固定乱序长度策略（forBoundedOutOfOrderness）
                         * 单调递增策略（forMonotonousTimestamps）
                         * 不生成策略（noWatermarks）
                         *
                         * forBoundedOutOfOrderness周期性生成水印
                         * 可更好处理延迟数据
                         *
                         *
                         * *********************/
                        //设置WaterMark的生成策略
                        .<EventPO>forBoundedOutOfOrderness(Duration.ofMillis(maxOutOfOrderness))
                        //将事件数据的事件时间提取出来
                        .withTimestampAssigner(serializableTimestampAssigner)
        );


        /* **********************
         *
         * 知识点：
         *
         * 9
         * Flume 导数据到 Kafka不是实时, 数据会发生延迟 1-2小时,
         * Flink计算数据是否会发生时间上的错乱, 导致数据统计不准确 ？
         *
         * 答案是不会 .
         *
         * Flink流计算的时间类型设置为EventTime，
         * 根据EventTime生成的watermark, 可以看作为EventTime世界里的时钟,
         * EventTime的取值是 Kafka 行为数据里的时间字段 ( event_time ),
         * event_time是固定的, 是已经写死在行为数据,
         * 即使 kafka的行为数据发生了乱序,
         * Flink也能根据watermark, 知道哪些数据是发生了乱序,
         * Flink是很清楚当前处理的数据是哪一个时刻的数据
         *
         *
         * 根据EventTime生成的watermark，
         * 将数据流形成一个带有时间标记的数据流。
         *
         * 10
         * 如果流计算的时间类型(语义)设置为EventTime,
         * 一定要生成watermark,
         * 否则程序会报错。
         *
         *
         * *********************/


        //Job Flink每5分钟计算用户最近1小时的登录次数
        DataStream<Tuple2<Integer,Integer>> ac =
        LoginFreqHourTask.process(eventPODataStreamWithWaterMark);

        /* **********************
         *
         * 为什么要写入 Redis ?
         * 因为Flink这里只是计算了最近1小时的登录次数,
         * 但如果指标需要最近2小时的登录次数呢? 最近3小时的登录次数呢?
         * 如何快速获取？难道要Flink再重新计算吗？
         *
         * 思路：
         * 在Redis将3个最近1小时的登录次数相加,
         * 不就可以得到最近3小时的登录次数,
         * 如此类推。
         *
         * 要做到这一步关键的就是 Redis key 的设计,
         * 这个key必须能够快速的定位到 最近1小时的登录次数
         *
         * 所以 Redis key 的格式为：
         * 指标id+uid+指标主维度+计算方式+指标版本号+编号
         *
         * 编号是指
         * 哪一个的 "最近1小时的登录次数" 才是 最近1小时,
         * 因为随着时间的推移,
         * 当前的 "最近1小时",
         * 相对于未来并不是 "最近1小时"
         *
         * 那么如何根据编号,在读取指标值的现在这一时间,
         * 距离这个当前时间的"最近1小时"的指标值是哪一个,
         * 这是一个难点。
         *
         *
         * *********************/


        env.execute();

    }

}
