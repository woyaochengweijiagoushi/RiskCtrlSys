package com.juege.RiskCtrlSys.commons.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * author: Juege
 * description: 测试Log4j2和Slf4j的日志输出
 * date: 2024
 */

@SpringBootTest(classes = Log4j2AndSlf4jTest.class)
public class Log4j2AndSlf4jTest {


    @DisplayName("测试Slf4j日志输出")
    @Test
    void testSlf4jOutput() {
        Log4j2AndSlf4jDemo.slf4jOutput();
    }

    @DisplayName("测试log4j2日志输出")
    @Test
    void testLog4j2Output() {
        Log4j2AndSlf4jDemo.log4j2Output();
    }
}