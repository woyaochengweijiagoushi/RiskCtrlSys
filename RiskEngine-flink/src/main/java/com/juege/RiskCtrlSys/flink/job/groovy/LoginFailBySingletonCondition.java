package com.juege.RiskCtrlSys.flink.job.groovy;

import com.googlecode.aviator.AviatorEvaluator;
import com.juege.RiskCtrlSys.flink.job.aviator.SumFunction;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;

import java.io.Serializable;

/**
 * author: Juege
 * description: TODO
 * date: 2024
 */

public class LoginFailBySingletonCondition<T> extends SimpleCondition<T> implements Serializable {

    /**
     * Aviator字段
     */
    private String field;
    /**
     * 规则表达式
     */
    private String expression;

    public LoginFailBySingletonCondition(String field, String expression) {
        this.field = field;
        this.expression = expression;

        //加载 Aviator 自定义函数
        AviatorEvaluator.addFunction(new SumFunction(this.field));
    }

    @Override
    public boolean filter(T eventPO) throws Exception {
//        Map<String,Object> params=new HashMap<>();
//        params.put("data",eventPO.getEvent_name());
//        //Aviator 表达式计算
//        return (Boolean) AviatorEvaluator.execute(expression,params);
        return true;
    }
}
