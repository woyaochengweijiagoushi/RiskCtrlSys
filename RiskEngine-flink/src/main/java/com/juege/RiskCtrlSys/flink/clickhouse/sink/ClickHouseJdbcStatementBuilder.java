package com.juege.RiskCtrlSys.flink.clickhouse.sink;

import org.apache.flink.connector.jdbc.JdbcStatementBuilder;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * author: Juege
 * description: 对sql语句的预编译 select * from xx where xx=? 2
 * date: 2024
 */

public class ClickHouseJdbcStatementBuilder<T> implements JdbcStatementBuilder<T> {

    /**
     * author: Juege
     * description:
     * @param preparedStatement: PreparedStatement对象
     * @param t:  clickhouse表的PO对象
     * @return void
     */
    @Override
    public void accept(PreparedStatement preparedStatement, T t) throws SQLException {

        /* **********************
         *
         * 知识点：
         *
         * 一。
         *
         * SQL 语句预编译：通过占位符实现
         *
         * 二。
         *
         * Java通过反射获取类的字段：
         *
         * 1. getDeclaredFields()：获取所有的字段，不会获取父类的字段
         * 2. getFields(): 只获取public字段，获取包含父类的字段
         *
         * *********************/

        Field[] fields = t.getClass().getDeclaredFields();

        //将获取到的字段替换sql预编译之后的占位符。
        try {
            ClickHouseJdbcSink.setPreparedStatement(preparedStatement, fields, t);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
