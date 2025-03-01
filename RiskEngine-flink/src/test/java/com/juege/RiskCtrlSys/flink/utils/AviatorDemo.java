package com.juege.RiskCtrlSys.flink.utils;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.HashMap;
import java.util.Map;

/**
 * author: Imooc
 * description: Aviator Demo
 * date: 2023
 */

/* **********************
 *
 * 知识点：
 *
 * 1.
 *
 * Avaitor的支持的逻辑运算符包括，
 * 否定运算符: !，
 * 逻辑与: &&，
 * 逻辑或: ||
 *
 *
 * 2.
 *
 * Aviator支持的关系运算符包括
 * "<" “<=” “>” “>=” 以及"==“和”!="
 *
 *
 * *********************/
public class AviatorDemo {
    public static void main(String[] args) {
        //数学运算
        Long result = (Long) AviatorEvaluator.execute("1+2+3");
        System.out.println(result);

        //逻辑运算
        System.out.println(AviatorEvaluator.execute("(1>0||0<1)&&1!=0"));

        //编译表达式
        String expression = "a>b";

        Integer n1 = 3;
        Integer n2 = 5;
        Map<String, Object> map = new HashMap<>(4);
        map.put("a", n1);
        map.put("b", n2);
        boolean b = (boolean)AviatorEvaluator.execute(expression, map);
        System.out.println(b);

        //自定义函数调用 imoocSum
        String exp = "imoocSum(b,c)";
        Map<String, Object> map2 = new HashMap<>();
        map2.put("a",1);
        map2.put("b",2);
        map2.put("c",3);

        Expression compiledExp = AviatorEvaluator.compile(exp,true);
        Double res =  (Double)compiledExp.execute(map2);
        System.out.println(res);

    }
}
