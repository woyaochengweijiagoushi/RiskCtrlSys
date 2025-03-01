package com.juege.RiskCtrlSys.flink.job.thread;

import com.juege.RiskCtrlSys.flink.utils.MysqlUtil;
import com.juege.RiskCtrlSys.model.MetricsConfPO;
import com.juege.RiskCtrlSys.utils.common.CommonUtil;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author: Juege
 * description: 读取Mysql存放的指标聚合计算规则线程
 * date: 2024
 */

/* **********************
 *
 * 知识点：
 *
 * 1.
 *
 * Runnable 和 Callable 区别：
 * Runnable 无返回值
 * Callable 有返回值
 *
 *
 * *********************/


public class MetricConfThread implements Callable<MetricsConfPO> {

    private PreparedStatement ps = null;

    /* **********************
     *
     * 知识点：
     *
     * 1.
     * ConcurrentHashMap 是线程安全
     *
     * *********************/
    private ConcurrentHashMap<String,String> metricMap = new ConcurrentHashMap<>();

    public MetricConfThread(PreparedStatement ps) {
        this.ps = ps;
    }

    /**
     * author: Juege
     * description: 向mysql读取指标聚合计算规则
     * @return MetricsConfPO:  MetricsConfPO 对象
     */
    private MetricsConfPO metricConfQuery() {
        /* **********************
         *
         * 注意：
         *
         * 需要及时释放 ResultSet 对象
         *
         * *********************/

        MetricsConfPO metricsConfPO = null;
        ResultSet rs = null;

        try {
            rs = MysqlUtil.executeQuery(ps);

            //将结果放到 Map
            /* **********************
             *
             * 知识点：
             *
             * 3
             * 第一次使用next()就将指针指向返回结果集的第一行。
             * 每使用一次next(),指针就指向下一行
             *
             *
             * *********************/
            while (rs.next()){

                //通过反射获取对象所有私有属性
                Field[] fields = MetricsConfPO.class.getDeclaredFields();
                //遍历 Field[]
                for(Field field:fields) {
                    String attrName = field.getName();
                    //需要判断Mysql表字段是否存在
                    if(CommonUtil.isExistColumn(rs,attrName)) {
                        metricMap.put(attrName,rs.getString(attrName));
                    }
                }

            }

            /* **********************
             *
             * 如何给 MetricsConfPO 对象的属性赋值 ?
             * 因为 metricMap 的 键(key) 是字符串,
             * 根据这个字符串,如何执行 MetricsConfPO 对应属性的 Setter 方法
             *
             * 方案
             * 通过反射机制
             *
             * *********************/

            //通过反射给 MetricsConfPO对象的属性赋值
            metricsConfPO = CommonUtil.setObjFieldsValue(MetricsConfPO.class,metricMap);
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            MysqlUtil.closeResultSet();
        }

        return metricsConfPO;
    }


    @Override
    public MetricsConfPO call() throws Exception {
       return metricConfQuery();
    }
}
