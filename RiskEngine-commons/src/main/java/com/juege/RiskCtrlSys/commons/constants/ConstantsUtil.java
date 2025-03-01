package com.juege.RiskCtrlSys.commons.constants;

/**
 * author: Juege
 * description: 常量工具类
 * date: 2024
 */

public class ConstantsUtil {

    /**
     * 项目路径
     */
    public static final String PROJECT_DIR = System.getProperty("user.dir");
    /**
     * 包路径
     */
    public static final String PACKAGE_DIR = "com/juege/RiskCtrlSys";

    /**
     * src路径
     */
    public static final String SRC_MAIN_JAVA = "/src/main/java/";
    /**
     * xml路径
     */
    public static final String XML_PATH = PROJECT_DIR + "/RiskEngine-dao/src/main/resources/mapper";
    /**
     * entity路径
     */
    public static final String ENTITY_PATH = PROJECT_DIR + "/RiskEngine-model/src/main/java/"+ PACKAGE_DIR +"/model/mapper";
    /**
     * mapper路径
     */
    public static final String MAPPER_PATH = PROJECT_DIR + "/RiskEngine-dao/src/main/java/"+ PACKAGE_DIR +"/dao/mapper";
    /**
     * service路径
     */
    public static final String SERVICE_PATH = PROJECT_DIR + "/RiskEngine-service/src/main/java/"+ PACKAGE_DIR +"/service/mapper";
    /**
     * serviceImpl路径
     */
    public static final String SERVICE_IMPL_PATH = PROJECT_DIR + "/RiskEngine-service/src/main/java/"+ PACKAGE_DIR +"/service/impl";
    /**
     * controller路径
     */
    public static final String CONTROLLER_PATH = PROJECT_DIR + "/RiskEngine-api/src/main/java/"+ PACKAGE_DIR +"/api/controller";

    /**
     * 父包路径
     */
    public static final String PARENT_PACKAGE = "com.juege.RiskCtrlSys";

    /**
     * 控制器包路径
     */
    public static final String CONTROLLER_PACKAGE = "api.controller";

    /**
     * 实体包路径
     */
    public static final String ENTITY_PACKAGE = "model.mapper";

    /**
     * 数据库映射包路径
     */
    public static final String MAPPER_PACKAGE = "dao.mapper";

    /**
     * 服务包路径
     */
    public static final String SERVICE_PACKAGE = "service.mapper";
    public static final String SERVICE_IMPL_PACKAGE = "service.impl";


    public static final String MAPPER_XML_PACKAGE = "dao.mapper";


    /**
     * 数据库url
     */
    public static final String MYSQL_URL = "jdbc:mysql://mysql:3306/juege?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false"
    ;
    /**
     * 数据库用户名
     */
    public static final String USERNAME = "root";
    /**
     * 数据库密码
     */
    public static final String PASSWORD = "123456";
    /**
     * 作者
     */
    public static final String AUTHOR = "juege";
    /**
     * 日期格式
     */
    public static final String DATE = "yyyy-MM-dd";
    /**
     * 工具模块名称
     */
    public static final String MODULE_UTILS = "RiskEngine-utils";

    /**
     * Flink模块名称
     */
    public static final String MODULE_FLINK = "RiskEngine-flink";
    /**
     * groovy脚本路径
     */
    public static final String GROOVY_SCRIPTS_PATH = "\\src\\main\\groovy\\scripts\\cep\\";

    /**
     * groovy脚本模板路径
     */
    public static final String GROOVY_SCRIPTS_TEMP_PATH = "\\src\\main\\groovy\\scripts\\cep\\template\\";



    /**
     * DDL_METRIC_ATTR变量保存了一个metric属性的定义信息。
     * metric_id: metric的id，类型为BIGINT。
     * metric_name: metric的名称，类型为STRING。
     * metric_sign: metric的符号，类型为STRING。
     * metric_code: metric的编码，类型为STRING。
     * scene: metric的应用场景，类型为STRING。
     * event: metric的事件，类型为STRING。
     * main_dim: metric的主维度，类型为STRING。
     * aggregation: metric的聚合方式，类型为STRING。
     * is_enable: metric是否启用，类型为STRING。
     * window_size: metric的窗口大小，类型为STRING。
     * window_step: metric的窗口步长，类型为STRING。
     * window_type: metric的窗口类型，类型为STRING。
     * flink_filter: metric在Flink中的过滤条件，类型为STRING。
     * flink_keyby: metric在Flink中的分键字段，类型为STRING。
     * flink_watermark: metric在Flink中的水印策略，类型为STRING。
     * metric_agg_type: metric的聚合类型，类型为STRING。
     * metric_store: metric的存储方式，类型为STRING。
     * datasource: metric的数据源，类型为STRING。
     * rule_id: metric所属的规则id，类型为BIGINT。
     */
    public static final String DDL_METRIC_ATTR = ""+
            "metric_id BIGINT,\n"+
            "metric_name STRING, \n" +
            "metric_code STRING, \n" +
            "scene STRING, \n" +
            "event STRING, \n" +
            "main_dim STRING, \n" +
            "aggregation STRING, \n" +
            "is_enable STRING, \n" +
            "window_size STRING, \n" +
            "window_step STRING, \n" +
            "window_type STRING, \n" +
            "flink_filter STRING, \n" +
            "flink_keyby STRING, \n" +
            "flink_watermark STRING, \n" +
            "metric_agg_type STRING, \n" +
            "metric_store STRING, \n" +
            "datasource STRING, \n" +
            "rule_id BIGINT, \n" +
            "PRIMARY KEY (metric_id) NOT ENFORCED \n"
            ;

    /**
     * 表名：风控指标属性
     */
    public static final String TABLE_NAME_METRIC_ATTR = "metric_attr";

    /**
     * Flink Job提交的参数Key：规则组唯一编码
     */
    public static final String ARGS_SET_CODE = "set.code";
    /**
     * Flink Job提交的参数Key：规则唯一编码
     */
    public static final String ARGS_RULE_CODE = "rule.code";
    /**
     * Flink Job提交的参数Key：groovy 模板名称
     */
    public static final String ARGS_GROOVY_NAME = "groovy.name";

    /**
     * 表名：风控规则组表
     */
    public static final String TABLE_NAME_RULE_SET = "rule_set";


    /**
     * 规则集的DDL定义
     */
    public static final String DDL_RULE_SET = ""
            + "auto_id BIGINT, \n" // 自动ID
            + "set_code STRING, \n" // 规则组编码
            + "rule_code STRING, \n" // 规则编码
            + "rule_set_name STRING, \n" // 规则集名称
            + "PRIMARY KEY (auto_id) NOT ENFORCED \n" // 主键(auto_id)，不强制执行
            ;


    /**
     * 表名：原子规则表
     */
    public static final String TABLE_NAME_RULE = "rule";
    /**
     * 路径：MetricRedisFunction类
     */
    public static final String PATH_CLASS_METRIC_REDIS_FUNCTION = "com.juege.RiskCtrlSys.flink.job.aviator.MetricRedisFunction";


}
