package com.juege.RiskCtrlSys.utils.hbase;

import com.juege.RiskCtrlSys.utils.Application;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * author: Juege
 * description: Hbase工具类单元测试
 * date: 2024
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = Application.class)
public class HbaseUtilTest {

    @Autowired
    private HbaseUtil hbaseUtil;

    @DisplayName("测试生成hbase Connection对象")
    @BeforeAll
    @Test
    public void testGetConnection() throws Exception {
        Connection conn = hbaseUtil.getConnection();
        System.out.println(conn);
    }

    @DisplayName("测试生成hbase Admin对象")
    @Test
    public void testGetAdmin() throws Exception {
        Admin admin = hbaseUtil.getAdmin();
        System.out.println(admin);
    }

}
