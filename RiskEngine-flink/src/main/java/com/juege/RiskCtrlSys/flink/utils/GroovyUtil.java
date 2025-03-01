package com.juege.RiskCtrlSys.flink.utils;

import com.juege.RiskCtrlSys.commons.constants.ConstantsUtil;
import com.juege.RiskCtrlSys.utils.common.CommonUtil;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.File;
import java.security.MessageDigest;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author: Juege
 * description: Groovy 脚本工具类
 * date: 2024
 */

public class GroovyUtil {

    /**
     * groovy GroovyClassLoader 对象 (用来解析Groovy脚本)
     */
    private static GroovyClassLoader LOADER = null;

    /**
     * 存放每个 Groovy 脚本内容的 MD5 指纹和所对应的groovy.lang.Script
     * key: md5指纹
     * value: groovy.lang.Script
     */
    private static ConcurrentHashMap<String, Class<Script>> clazzMaps
            = new ConcurrentHashMap<String, Class<Script>>();



    /**
     * author: Juege
     * description: 生成 Groovy 脚本编译的参数配置对象
     * @param :
     * @return org.codehaus.groovy.control.CompilerConfiguration
     */
    private static CompilerConfiguration getCompilerConfiguration() {
        CompilerConfiguration config = new CompilerConfiguration();
        return config;
    }

    /**
     * author: Juege
     * description: 生成 GroovyClassLoader 对象
     * @param :  
     * @return groovy.lang.GroovyClassLoader 
     */
    public static GroovyClassLoader getEngineByClassLoader(String key) {

        GroovyClassLoader groovyClassLoader = null;
        Class<Script> script = clazzMaps.get(key);
        //每个groovy脚本单独创建一个GroovyClassLoader对象
        if ( script == null ) {
            synchronized (key.intern()) {
                // Double Check
                script = clazzMaps.get(key);
                if ( script == null ) {
                    groovyClassLoader = new GroovyClassLoader(
                            //GroovyClassLoader的父ClassLoader为当前线程的加载器
                            Thread.currentThread().getContextClassLoader(),
                            //Groovy 脚本编译的参数配置
                            getCompilerConfiguration()
                    );
                }
            }
        }

        /* **********************
         *
         * 知识点：
         *
         * 这里涉及有3个概念：
         * 1. groovy.lang.Script： Groovy能加载的脚本必须是继承自groovy.lang.Script
         * 2. CompilerConfiguration： 设置编译的一些参数
         * 3. GroovyClassLoader：提供解析Groovy脚本文件的能力
         *
         *
         * *********************/

        return groovyClassLoader;

    }

    /**
     * author: Juege
     * description: 获取Groovy脚本文件
     * @param groovyClass:
     * @return java.io.File
     */
    private static File getClassByFile(String groovyClass) {
        //获取模块路径
        String modulePath = CommonUtil.getModulePath(ConstantsUtil.MODULE_FLINK);
        //获取脚本完整路径
        String _path = modulePath + ConstantsUtil.GROOVY_SCRIPTS_PATH;
        String path = _path+groovyClass+".groovy";
        return new File(path);
    }

    /**
     * author: Juege
     * description: 解析 groovy 脚本
     * @param groovyClass:
     * @return void
     */
    public static Object groovyEval(
            String groovyClass,
            String method,
            Object args) {

        //设置需要返回的对象为null
        Object obj = null;

        //获取脚本文件
        File file = getClassByFile(groovyClass);

        /* **********************
         *
         * 注意：
         *
         * 为什么要生成 GroovyClassLoader单例
         * 因为要控制GroovyClassLoader实例的数量, 节约系统资源
         *
         * 判断是否是同一个groovy脚本，是使用脚本文件的MD5值进行判断,
         * 如果是同一个groovy脚本, 只会生成对应的一个GroovyClassLoader实例
         *
         *
         * *********************/

        /* 使用GroovyClassLoader动态地加载一个脚本   ***/

        //获取文件的md5编码
        String md5 = fingerKey(fileToString(file));
        //根据md5编码获取GroovyClassLoader单例
        LOADER = getEngineByClassLoader(md5);


        try {
            //解析脚本
            Class<?> groovyScript = LOADER.parseClass(file);



            /* **********************
             *
             * 注意：
             *
             * 为什么要将 Class<?> 泛型转换为 groovy.lang.Script 对象 ?
             *
             * 因为 groovy 将groovy脚本解析后生成的是groovy.lang.Script类，
             * 所以 Class<?> , 这里的泛型指代的就是 groovy.lang.Script 对象，
             * 这里也只是强制转换一下，其实不转换也没有问题，
             * 只是要清楚Class<?> , 泛型指代的就是 groovy.lang.Script 对象就可以了
             *
             *
             * *********************/

            //将 Class<?> 泛型转换为 groovy.lang.Script 对象
            Class<Script> _groovyScript = (Class<Script>) groovyScript;


            //将文件md5编码以及所对应的 Class<Script> 对象写入HashMap,
            //用于下次的GroovyClassLoader单例判断
            clazzMaps.put(md5, _groovyScript);


            // 获得实例
            GroovyObject groovyObject = (GroovyObject) groovyScript.newInstance();
            // 反射调用方法
            obj = groovyObject.invokeMethod(method, args);

            //返回
            return obj;

        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    /**
     * author: Juege
     * description: 生成groovy脚本代码的md5指纹
     * @param scriptText:
     * @return java.lang.String 
     */
    private static String fingerKey(String scriptText) {
        try {
            // 获取MD5加密实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 将脚本文本转换为字节数组，并进行MD5加密
            byte[] bytes = md.digest(scriptText.getBytes("utf-8"));

            // 准备十六进制字符数组，用于转换加密后的字节数据
            final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
            // 使用StringBuilder存储转换后的十六进制字符串
            StringBuilder ret = new StringBuilder(bytes.length * 2);
            // 遍历加密后的字节数组，转换为十六进制字符串
            for (int i=0; i<bytes.length; i++) {
                // 处理每个字节的高4位和低4位，转换为对应的十六进制字符
                ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
                ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
            }
            // 返回转换完成的十六进制字符串
            return ret.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * author: Juege
     * description: File转String
     * @param file:
     * @return java.lang.String
     */
    private static String fileToString(File file) {
        //读取File对象内容转为String
        return CommonUtil.fileToString(file);
    }

    public static void redisLoader() {

        //TODO
    }

    public static void registerScriptWithRedis() {

        //TODO
    }
}
