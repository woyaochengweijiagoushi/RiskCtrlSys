package com.juege.RiskCtrlSys.api.exception;

import com.juege.RiskCtrlSys.commons.exception.custom.RedisException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * author: Juege
 * description: 全局的异常捕抓 (Api)
 * date: 2024
 */

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = RedisException.class)
    public void RedisExceptionHandler(RedisException e) {
        System.out.println("RedisExceptionHandler!!!!!!!");
        //TODO 错误处理
    }

}
