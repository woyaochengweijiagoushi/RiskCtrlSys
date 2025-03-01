package com.juege.RiskCtrlSys.commons.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * author: Juege
 * description: Log4j2和Slf4j输出Demo
 * date: 2024
 */

@Slf4j
public class Log4j2AndSlf4jDemo {

    private static final Logger logger = LogManager.getLogger(Log4j2AndSlf4jDemo.class);

    /**
     * author: Juege
     * description: slf4j输出
     * @param :
     * @return void
     */
    public static void slf4jOutput() {
        log.warn("this is slf4j output");
    }

    /**
     * author: Juege
     * description: log4j2输出
     * @param :
     * @return void
     */
    public static void log4j2Output() {
        logger.error("this is log4j2 error output");
        logger.info("this is log4j2 info output");
    }
}
