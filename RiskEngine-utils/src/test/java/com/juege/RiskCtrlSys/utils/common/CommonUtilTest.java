package com.juege.RiskCtrlSys.utils.common;

import com.juege.RiskCtrlSys.commons.constants.ConstantsUtil;
import com.juege.RiskCtrlSys.model.EventPO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * author: Juege
 * description: 公共工具类单元测试
 * date: 2024
 */

public class CommonUtilTest {

    @DisplayName("测试通过反射机制执行字符串拼接的对象属性Getter方法")
    @Test
    void testGetFieldValue() {

        EventPO eventPO = new EventPO();
        eventPO.setUser_id_int(3);

        String keyByStr = "user_id_int";
        Integer uid = (Integer) CommonUtil.getFieldValue(eventPO,keyByStr);

        System.out.println(uid);
        assertEquals(
                3,
                uid
        );

    }

    @DisplayName("测试从Mysql查询的结果集中是否指定字段")
    @Test
    void testIsExistColumn() {

    }


    @DisplayName("测试通过反射给对象属性赋值")
    @Test
    void testSetObjFieldsValue() {

        Map<String,String> map = new HashMap<>();
        map.put("event_name","LOGIN");
        EventPO eventPO = CommonUtil.setObjFieldsValue(EventPO.class,map);
        System.out.println(eventPO.getEvent_name());
        assertEquals(
                "LOGIN",
                eventPO.getEvent_name()
        );
    }

    @DisplayName("测试Mysql存放指标计算的filter部分的切割 (字符串转数组)")
    @Test
    void testMetricFiltersplit() {
        //只有1个filter字段
        String filter_1 = "1*|event_name::pay";
        String[] filters_1 = CommonUtil.metricFiltersplit(filter_1);
        for(String f:filters_1) {
            System.out.println(f);
        }
        System.out.println("###############");
        //只有2个filter字段
        String filter_2 = "2*|event_name::pay,event_name::order*|&&";
        String[] filters_2 = CommonUtil.metricFiltersplit(filter_2);
        for(String f:filters_2) {
            System.out.println(f);
        }
        System.out.println("###############");
        //只有3个filter字段
        String filter_3 = "3*|event_name::pay,event_name::order,event_name::browse*|&&,||";
        String[] filters_3 = CommonUtil.metricFiltersplit(filter_3);
        for(String f:filters_3) {
            System.out.println(f);
        }
    }

    @DisplayName("测试取出filter部分的字段和值")
    @Test
    void testGetFilterKeyAndValue() {
//        String[] filter = {"event_name::pay","event_name::order::&&"};
        String[] filter = {"event_name::pay","event_name::order::&&","event_name::browse::||"};
//        String[] filter = {"event_name::pay"};
        Map<String,Map<String,String>> map = CommonUtil.getFilterKeyAndValue(filter);
        Set<String> set_1 = map.keySet();
        Iterator<String > it_1 = set_1.iterator();
        while (it_1.hasNext()){
            String key_1 = it_1.next();
            System.out.println(key_1);
            Map<String ,String> map_1 = map.get(key_1);

            Set<String> set_2 = map_1.keySet();
            Iterator<String > it_2 = set_2.iterator();
            while (it_2.hasNext()){
                String key_2 = it_2.next();
                System.out.println(key_2);
                System.out.println(map_1.get(key_2));

            }
        }
    }

    @DisplayName("测试File对象转字符串")
    @Test
    void testFileToString() {

        //文件名
        String fileName = "LoginFailBySingleton.groovy";

        //文件所在模块路径
        String modulePath = CommonUtil.getModulePath(ConstantsUtil.MODULE_FLINK);
        //完整路径
        String path = modulePath + ConstantsUtil.GROOVY_SCRIPTS_PATH + fileName;
        //将File对象内容转为字符串
        String str =CommonUtil.fileToString(new File(path));

        System.out.println(str);

    }

    @DisplayName("测试替换文件内容的占位符")
    @Test
    void testReplaceString() {

        //文件名
        String fileName = "Circulate.groovy";

        //文件所在模块路径
        String modulePath = CommonUtil.getModulePath(ConstantsUtil.MODULE_FLINK);
        //完整路径
        String path = modulePath + ConstantsUtil.GROOVY_SCRIPTS_TEMP_PATH + fileName;
        //
        HashMap<String,String> map = new HashMap<>();
        map.put("__START__","begin");
        map.put("__TIMES__","3");

        String str = CommonUtil.replaceString(new File(path),map);
        System.out.println(str);

    }

}
