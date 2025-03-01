package com.juege.RiskCtrlSys.utils.hbase;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * author: Juege
 * description: Hbase配置类
 * date: 2024
 */


/* **********************
 *
 * 知识点：
 *
 * @EnableConfigurationProperties不能单独使用
 * @EnableConfigurationProperties 和 @ConfigurationProperties组合使用
 *
 * @EnableConfigurationProperties将@ConfigurationProperties所修饰的类添加到IOC容器里
 *
 * *********************/



@Configuration
@EnableConfigurationProperties(HbaseProperties.class)
public class HbaseConf {

    @Autowired
    private HbaseProperties hbaseProperties;

    /**
     * author: Juege
     * description: 将Hbase配置信息添加到HBaseConfiguration
     * @param :
     * @return org.apache.hadoop.conf.Configuration
     */
    public org.apache.hadoop.conf.Configuration configuration() {
        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
        Map<String,String> map = hbaseProperties.getConfMaps();
        map.forEach((k,v)->{
            conf.set(k,v);
        });

        return conf;
    }
}
