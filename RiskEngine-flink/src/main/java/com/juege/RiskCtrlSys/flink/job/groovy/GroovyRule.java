package com.juege.RiskCtrlSys.flink.job.groovy;

import org.apache.flink.cep.pattern.Pattern;

/**
 * Groovy脚本 Flink-Cep 返回Pattern的接口
 * @param <T>
 */
public interface GroovyRule<T> {

     /**
      * author: Juege
      * description: 返回 Flink-Cep Pattern结构体
      * @param :
      * @return org.apache.flink.cep.pattern.Pattern<T,?>
      */
     Pattern<T,?> getPattern();
}
