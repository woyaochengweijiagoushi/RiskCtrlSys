package com.juege.RiskCtrlSys.utils.common;

import com.juege.RiskCtrlSys.commons.constants.ConstantsUtil;
import com.juege.RiskCtrlSys.model.EventPO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: Juege
 * description: 公共工具类
 * date: 2024
 */

public class CommonUtil {

    /* **********************
     *
     * 知识点：
     *
     * 1.
     * xx(Class<T>): 这个方法需要传入一个泛型类的参数,
     * 传入实参应该是 xxx.class
     *
     * 2.
     * static <T> T 指 返回值是 T 这个泛型的数据类型
     *
     *
     *
     *
     * *********************/

    /**
     * author: Juege
     * description: 通过反射给对象属性赋值
     * @param obj: Class对象
     * @param map:  Map<属性名,属性值> 注意：属性值都是字符串类型
     * @return null
     */
    public static <T> T setObjFieldsValue(Class<T> obj, Map<String,String> map) {

        /* **********************
         *
         * 这个方法有局限性：
         * 1. 只能用于 Setter 方法 setXx()
         * 2. 属性数据类型只能是字符串
         *
         * *********************/

        T instance = null;

        /* **********************
         *
         * 知识点：
         *
         * 3.
         *
         * 反射机制调用对象函数方法步骤：
         * a. newInstance() 创建对象实例
         * b. getMethod(方法名,入参的数据类型) 返回当前对象的指定函数方法 的 Method 对象
         * c. invoke(对象实例,入参) 执行函数方法
         *
         *
         * *********************/

        try {
            //通过公共的空参的构造方法创建对象
            instance = obj.newInstance();

            //获取对象所有属性,包括私有属性
            Field[] fields = obj.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                //获取属性名
                String attrName = fields[i].getName();

                //判断map中是否存在属性名的Key
                if(map.containsKey(attrName)) {
                    //判断属性类型是否String (只针对String的反射)
                    if(fields[i].getType() == String.class) {

                        //方法名称的组装
                        String methodName = methodNameFormat(attrName, "set");

                        /* **********************
                         *
                         * 知识点：
                         *
                         * Java反射：
                         * 1.
                         * getMethod(方法名,形参数据类型)
                         *
                         * 2.
                         * getMethod() 形参数据类型为String, 是 String.class
                         * 但 getMethod() 形参是基础数据类型时，
                         * 一定注意不要传入 Integer.class，
                         * 而应该是 int.class,
                         * 因为 Integer.class 传入的只是包装类 Integer的class,而不是int的class
                         *
                         *
                         *
                         * *********************/
                        //获取当前属性的Setter方法,Setter方法必须public
                        Method setMethod = obj.getMethod(methodName, String.class);
                        //执行该Setter方法
                        setMethod.invoke(instance, map.get(attrName));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
//            throw new UtilException(UtilExceptionInfo.INVOKE_METHOD_NULL);
        }

        return instance;
    }


    /**
     * author: Juege
     * description: Mysql存放指标计算的filter部分的切割 (字符串转数组)
     * @param str:
     * @return java.lang.String[]: 返回格式 [event_name::pay,event_name::order::&&]
     */
    public static String[] metricFiltersplit(String str) {

        /* **********************
         *
         * filter部分Mysql存放格式：
         *
         * # 只有1个filter字段
         * 1*|filter字段::值
         *
         * # 2个filter字段
         * 2*|filter字段1::值1,filter字段2::值2*|逻辑关系符
         *
         * # 3个filter字段
         * 3*|filter字段1::值1,filter字段2::值2,filter字段3::值3*|逻辑关系符1,逻辑关系符2
         *
         * 最多只能有3个filter字段
         * 下面的代码不通用,也不灵活
         * 这个根据自己业务自定义格式就可以了
         *
         * *********************/


        //取出filter字段数量 (注意这里的 | 要转义)
        String[] splits = str.split("\\*\\|");
        if(splits[0].equals("1")) {
            //直接返回 如：[event_name::pay]
           String[] res = {splits[1]};
           return res;
        }else if(splits[0].equals("2")) {
            //对2个filter字段切割
            String[] splits_field = splits[1].split(",");
            //取出逻辑符号
            String logic = splits[2];
            //逻辑符号拼接到第2个filter字段
            String field_and_logic = splits_field[1] + "::" + logic;
            //直接返回 如：[event_name::pay,event_name::pay::&&]
            String[] res = {splits_field[0],field_and_logic};
            return res;

        }else if(splits[0].equals("3")) {

            //对3个filter字段切割
            String[] splits_field = splits[1].split(",");
            //对2个逻辑符号切割
            String[] logics = splits[2].split(",");
            //逻辑符号拼接到第2个filter字段
            String field_and_logic1 = splits_field[1] + "::" + logics[0];
            //逻辑符号拼接到第3个filter字段
            String field_and_logic2 = splits_field[2] + "::" + logics[1];
            //直接返回 如：[event_name::pay,event_name::order::&&,event_name::browse::||]
            String[] res = {splits_field[0],field_and_logic1,field_and_logic2};

            return res;

        }

        return null;
    }


    /**
     * author: Juege
     * description: 取出filter部分的字段和值
     * @param filters: [event_name::pay] or
     *                 [event_name::pay,event_name::order::&&] or
     *                 [event_name::pay,event_name::order::&&,event_name::browse::||]
     * @return java.util.Map<java.lang.String,java.lang.String>
     */
    public static Map<String,Map<String,String>> getFilterKeyAndValue(String[] filters) {

        /* **********************
         *
         * 下面这代码也是不通用,不灵活
         * 大家根据自己业务自定义格式就可以了
         *
         * *********************/


        /* **********************
         *
         * 注意这里是使用 LinkedHashMap,
         * 不是 HashMap
         *
         * *********************/
        Map<String,Map<String,String>> map = new LinkedHashMap<>();

        if(filters.length==1) {
            /* **********************
             *
             * 注意这里是使用 IdentityHashMap,
             * 不是 HashMap
             *
             * *********************/
            Map<String,String> _map = new IdentityHashMap<>();
            //filter部分只有1个字段
            String[] values = filters[0].split("::");
            _map.put(values[0],values[1]);

            //返回格式：{null:{key:value}}
            map.put("null",_map);
        }else if(filters.length==2) {
            Map<String,String> _map = new IdentityHashMap<>();
            //filter部分有2个字段
            String[] values1 = filters[0].split("::");
            String[] values2 = filters[1].split("::");
            _map.put(values1[0],values1[1]);
            _map.put(values2[0],values2[1]);

            //返回格式：{&&:{key1:value1,key2:value2}}
            map.put(values2[2],_map);
        }else if(filters.length==3) {
            Map<String,String> _map1 = new IdentityHashMap<>();
            Map<String,String> _map2 = new IdentityHashMap<>();
            //filter部分有3个字段
            String[] values1 = filters[0].split("::");
            String[] values2 = filters[1].split("::");
            String[] values3 = filters[2].split("::");
            _map1.put(values1[0],values1[1]);
            _map1.put(values2[0],values2[1]);
            _map2.put(values3[0],values3[1]);

            //返回格式：{&&:{key1:value1,key2:value2},||:{key3:value3}}
            map.put(values2[2],_map1);
            map.put(values3[2],_map2);
        }

        return map;
    }

    public static boolean getFilterValue(EventPO eventPO, Map<String,Map<String,String>> map) {


        /* **********************
         *
         * 知识点：
         *
         * 1.
         * 嵌套 map 遍历步骤：
         *
         * 2.
         * 通过反射将字符串转换为方法名
         *
         *
         * *********************/

        //获取Set集合
        Set<Map.Entry<String,Map<String,String>>> entrySet = map.entrySet();
        //迭代器存放嵌套集合
        Iterator<Map.Entry<String,Map<String,String>>> it = entrySet.iterator();
        while (it.hasNext()){
            //获取嵌套对象
            Map.Entry<String,Map<String ,String >> ss = it.next();
            //ss.getKey(),
            //ss.getValue() 存放第2层的map

            //获取第2层map
            Map<String,String> map2 = ss.getValue();
            Set<Map.Entry<String ,String >> entrySet2 = map2.entrySet();
            Iterator<Map.Entry<String,String >> it2 = entrySet2.iterator();

            //多个字段条件逻辑为且的标记位
            int logic_and = 0;
            //多个字段条件逻辑为或的标记位
            int logic_or = 0;
            while (it2.hasNext()){

                Map.Entry<String ,String > ss2 = it2.next();

                //filter部分只有1个字段
                if("null".equals(ss.getKey())) {
                    try {
                        String attrName = ss2.getKey();
                        String methodName = attrName.substring(0, 1).toUpperCase() + attrName.substring(1);
                        Method getMethod = eventPO.getClass().getMethod("get" + methodName, String.class);
                        String event_name = (String) getMethod.invoke(eventPO, null);
                        if(event_name.equals(ss2.getValue())){
                            return true;
                        }
                    }catch (Exception e){

                    }
                }else if("&&".equals(ss.getKey())) {
                    //filter有2个字段 且 2个字段的逻辑关系是 &&
                        try {
                            String attrName = ss2.getKey();
                            String methodName = attrName.substring(0, 1).toUpperCase() + attrName.substring(1);
                            Method getMethod = eventPO.getClass().getMethod("get" + methodName, String.class);
                            String event_name = (String) getMethod.invoke(eventPO, null);
                            if(event_name.equals(ss2.getValue())){
                                logic_and += 1;
                            }
                        }catch (Exception e){

                        }
                }else if("||".equals(ss.getKey())) {
                    //filter有2个字段 且 2个字段的逻辑关系是 &&
                    try {
                        String attrName = ss2.getKey();
                        String methodName = attrName.substring(0, 1).toUpperCase() + attrName.substring(1);
                        Method getMethod = eventPO.getClass().getMethod("get" + methodName, String.class);
                        String event_name = (String) getMethod.invoke(eventPO, null);
                        if(event_name.equals(ss2.getValue())){
                            logic_or = 1;
                        }
                    }catch (Exception e){

                    }
                }

            }

            if(logic_and > 1) {
                return true;
            }

            if(logic_or == 1) {
                return true;
            }

        }

        return false;

    }

    /**
     * author: Juege
     * description: 通过反射机制执行字符串拼接的对象属性Getter方法
     * @param obj: 对象实例
     * @param str:
     * @return java.lang.Integer
     */
    public static <T> Object getFieldValue(T obj,String str) {

        /* **********************
         *
         * 这个方法有局限性：
         *
         * 1. 只能用于 Getter 方法 getXx()
         * 2. Getter 是无参的
         *
         * *********************/


        Object object = null;
        String methodName = null;

        //方法名称组装
        methodName = methodNameFormat(str,"get");

        try {
            //这里只考虑无参的方法
            Method getMethod = obj.getClass().getMethod(methodName);
            object = getMethod.invoke(obj);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        return object;

    }

    /**
     * author: Juege
     * description: 方法名称的组装
     * @param str:
     * @param preStr:
     * @return java.lang.String
     */
    private static String methodNameFormat(String str,String preStr) {

        String methodName = null;
        /* **********************
         *
         * 知识点：
         *
         * 字符串是否为空的判断
         *
         * *********************/

        if(preStr == null || preStr.length() == 0) {
            methodName = str;
        }else {
            //将首字母变为大写，准备组装方法名
            methodName = preStr + str.substring(0, 1).toUpperCase() + str.substring(1);
        }

        return methodName;
    }

    /**
     * author: Juege
     * description: 判断ResultSet结果集中是否存在指定列
     * @param rs: ResultSet结果集
     * @param columnName: 指定列
     * @return boolean 
     */
    public static boolean isExistColumn(ResultSet rs, String columnName) {
        try {
            if (rs.findColumn(columnName) > 0 ) {
                return true;
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

        return false;
    }


    /**
     * author: Juege
     * description: 获取模块路径
     * @param module:  模块名
     * @return java.lang.String
     */
    public static String getModulePath(String module) {

        //项目根路径
        String rootPath = System.getProperty("user.dir");
        //模块路径
        return rootPath.replace(ConstantsUtil.MODULE_UTILS,module);

    }

    /**
     * author: Juege
     * description: File对象转字符串
     * @param file:  File 对象
     * @return java.lang.String
     */
    public static String fileToString(File file) {
        StringBuilder sb = new StringBuilder();
        try {
            // 创建一个BufferedReader对象，用于读取文件
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s = null;
            // 读取文件的每一行，直到文件末尾
            while ((s = br.readLine())!= null) {
                // 将每一行内容追加到StringBuilder对象中
                sb.append(s);
            }
            // 关闭BufferedReader对象
            br.close();
        } catch (IOException e) {
            // 输出异常信息
            e.printStackTrace();
        }
        // 将StringBuilder对象转换为字符串并返回
        return sb.toString();

    }

    /**
     * author: Juege
     * description: 替换文件内容的占位符
     * @param file: 文件内容
     * @param map:  要替换的内容, 占位符名称为Key
     * @return File
     */
    public static String replaceString(File file,HashMap<String,String> map) {
        //新内容
        StringBuffer newString = new StringBuffer();
        try {
            //读取流
            BufferedReader br = new BufferedReader(new FileReader(file));
            //旧内容
            String oldString = null;
            //将要替换的占位符格式化 %(key1|key2)%
            String patternString = "%(" + StringUtils.join(map.keySet(), "|") + ")%";
            //使用 java.util.regex.Pattern 正则对文件的占位符进行替换
            Pattern pattern = Pattern.compile(patternString);

            //读取文件
            while ((oldString = br.readLine()) != null){

                Matcher matcher = pattern.matcher(oldString);

                //正则判断
                while(matcher.find()) {
                    //替换占位符
                    matcher.appendReplacement(newString, map.get(matcher.group(1)));
                }
                matcher.appendTail(newString);
                //换行
                newString.append(System.lineSeparator());
            }
            //关闭流
            br.close();
        } catch(Exception e){
            System.out.println(e.getMessage());
        }

        return newString.toString();
    }
}
