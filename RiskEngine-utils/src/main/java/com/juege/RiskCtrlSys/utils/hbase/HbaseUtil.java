package com.juege.RiskCtrlSys.utils.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.apache.hadoop.hbase.client.ConnectionFactory.createConnection;

/**
 * author: Juege
 * description: Hbase工具类
 * date: 2024
 */

@Component
public class HbaseUtil {

    @Autowired
    private HbaseConf hbaseConf;

    private Connection connection = null;
    private Admin admin = null;

    /**
     * author: Juege
     * description: 生成连接
     * @param :
     * @return org.apache.hadoop.hbase.client.Connection
     */
    public Connection getConnection() throws IOException {

        /* **********************
         *
         * 知识点：
         *
         * 连接Hbase的正确姿势是所有的进程共用一个Connection对象
         *
         * *********************/

        /* **********************
         *
         * 使用双重验证的单例模式生成Connection对象
         *
         * *********************/

        if(connection == null) {
            synchronized (HbaseUtil.class) {
                if (connection == null) {
                    Configuration conf = hbaseConf.configuration();
                    connection = ConnectionFactory.createConnection(conf);
                }
            }
        }

        return connection;

    }

    /**
     * author: Juege
     * description: 生成Admin
     * @param :
     * @return org.apache.hadoop.hbase.client.Admin
     */
    public Admin getAdmin() throws IOException {
       admin = connection.getAdmin();
       return admin;
    }

}
