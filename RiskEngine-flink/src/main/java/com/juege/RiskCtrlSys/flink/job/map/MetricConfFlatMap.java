package com.juege.RiskCtrlSys.flink.job.map;

import com.juege.RiskCtrlSys.flink.job.thread.MetricConfThread;
import com.juege.RiskCtrlSys.flink.utils.MysqlUtil;
import com.juege.RiskCtrlSys.model.EventPO;
import com.juege.RiskCtrlSys.model.MetricsConfPO;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * author: Juege
 * description: 指标配置参数读取
 * date: 2024
 */

public class MetricConfFlatMap extends RichFlatMapFunction<EventPO,EventPO> {

    private ParameterTool parameterTool = null;
    private PreparedStatement ps = null;
    private Connection conn = null;

    /* **********************
     *
     * 思路：
     *
     * 目标
     * 在RichFlatMapFunction读取Mysql的指标计算配置,
     * 指标计算配置写到事件流,
     * 将指标计算配置向下游的算子传播
     *
     * 步骤
     * 1. 在 open() 连接Mysql, 获取 Connection 对象
     * 2. 在 flatMap() 单独起一个线程周期性读取Mysql,
     *    所以需要周期性的线程池 Executors.newScheduledThreadPool()
     * 3. 在 flatMap(),从线程的返回值去获取Mysql的查询信息
     * 4. 在 flatMap(),将指标计算配置写入到事件流向下传播
     *
     *
     *
     * *********************/


    /**
     * author: Juege
     * description: 定时读取Mysql的指标配置参数,写入 EventPO 对象里,
     * @param eventPO:
     * @param collector:
     * @return void
     */
    @Override
    public void flatMap(EventPO eventPO, Collector<EventPO> collector) throws Exception {

        /* **********************
         *
         * 指标计算配置和行为事件进行匹配 的思路：
         *
         * 1.
         * 为什么要指标计算配置和行为事件进行匹配 ?
         * 因为指标计算是要根据行为事件进行计算的,
         * 但指标计算配置有很多个, 每个指标计算配置都是针对特定的行为事件
         *
         * 2.
         * 这里涉及3个元素：
         * a. 行为事件
         * b. 风控规则
         * c. 规则的指标计算配置
         *
         * 3.
         * 匹配关系是：
         * a. 指标计算配置 和 风控规则匹配 (根据规则id)
         * b. 行为事件 和 风控规则匹配 (根据 event_name)
         * 所以如果行为事件携带了对应的风控规则id, 就能获取到对应的指标计算配置
         * 然后就能根据对应的指标计算配置动态的进行指标计算
         *
         * 4.
         * 那么问题就转化为行为事件如何能携带风控规则id ?
         * 难点是行为事件可以匹配多个的风控规则,
         * 难道要行为事件的POJO对象写入存储风控规则的列表,
         * 然后循环风控规则的列表, 获取对应的指标计算配置,
         * 这样子的做法太 low 了。
         *
         * 5.
         * 解决思路：
         * a. 以原子规则表作为数据源, 生成原子规则表的事件流
         * b. 根据每一条的原子规则,对原子规则表的事件流进行拆分(OutTag), 有多少条原子规则, 就拆分多少条事件流
         *    拆分后的每一条事件流, 携带的数据, 就只有单一条的原子规则
         * c. 拆分后的每一条事件流, 去消费kafka的行为事件数据,
         *    过滤出匹配这一条事件流携带的原子规则所需要的行为事件,
         *    并将原子规则信息写入行为事件POJO,
         *    所以每一条的事件流的行为事件, 就携带上了对应的原子规则信息
         *
         * *********************/
        //行为事件名称
        String event_name = eventPO.getEvent_name();
        //规则id
        String rule_code = eventPO.getSingleRule().getRule_code();
        //查询指标状态是开启,且指标计算类型是Flink聚合计算的指标
        //本项目的原子规则限定只有1个指标, 所以查询出来的只有1个对应的指标配置
        String sql =
                "select * from metric_attr "+
                "where is_enable='true' "+
                "and metric_type='flink' "+
                "and event='"+event_name+"' "+
                "and rule_code='"+rule_code+"'"
                ;
        //获取PreparedStatement对象
        ps = MysqlUtil.initPreparedStatement(sql);


        /* **********************
         *
         * 注意：
         *
         *
         * 定时任务不建议使用Timer
         * 因为 Timer里边的逻辑失败的话不会抛出任何异常
         *
         * 建议用ScheduledExecutorService替换Timer
         *
         *
         * *********************/


        //定时读取Mysql, 只需要启动1个线程就可以了
        ScheduledExecutorService executorService =
                Executors.newScheduledThreadPool(1);

        /* **********************
         *
         * 知识点：
         *
         * 2
         * scheduleAtFixedRate有4个入参：
         * a. 执行的任务
         * b. 任务开始的时间
         * c. 任务之间相隔的时间
         * d. 时间单位
         *
         * 3.
         * ScheduledExecutorService 继承了 ThreadPoolExecutor,
         * ScheduledExecutorService 有2个方法：
         * a. scheduleAtFixedRate()
         * b. scheduleWithFixedDelay()
         *
         * 4.
         * scheduleAtFixedRate() 和 scheduleWithFixedDelay() 区别：
         * scheduleAtFixedRate() 是不管上一次任务完成,直接执行
         * scheduleWithFixedDelay() 是需要等待上一次任务完成,才执行
         *
         * 5.
         * 获取线程返回值
         * 按道理应该要实现Callable接口,
         * 但scheduleAtFixedRate只接收Runnable接口,
         * 那要获取线程返回值,怎办 ?
         *
         * 方案1:
         * 手动自定义创建线程池,
         * 这是阿里内部推荐的做法,
         * newScheduledThreadPool 内部是调用 ScheduledThreadPoolExecutor,
         * ScheduledThreadPoolExecutor的构造函数有 ThreadFactory 对象,
         * 继承这个对象进行自定义就可以了
         *
         * 方案2:
         * 使用 schedule(),这个方法可以接收Callable
         *
         *
         *
         * *********************/

        //每隔10秒执行向mysql读取指标聚合计算规则
        ScheduledFuture<MetricsConfPO> future =
        executorService.schedule(
                new MetricConfThread(ps),
                10L,
                TimeUnit.SECONDS
        );

        //获取线程返回值
        MetricsConfPO metricsConfPO = (MetricsConfPO) future.get();
        //写入到 EventPO
        eventPO.setMetrics_conf(metricsConfPO);
        //输出到事件流
        collector.collect(eventPO);
    }

    /**
     * author: Juege
     * description: 读取 指标的聚合计算的规则
     * @param parameters:
     * @return void
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);


        /* **********************
         *
         * 知识点
         *
         * 1.
         * open() 在所有方法之前执行，只执行一次
         * 在 open() 里进行数据库的连接，在整个流计算过程中都是使用这个数据库连接
         *
         *
         * *********************/

        //获取全局 ParameterTool
        parameterTool = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();

        //获取 Connection 对象
        conn = MysqlUtil.init(parameterTool);

    }

}
