package com.juege.RiskCtrlSys.dao;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.juege.RiskCtrlSys.commons.constants.ConstantsUtil;
import com.juege.RiskCtrlSys.dao.CodeGenerator.GlobalConfigGenerator;
import com.juege.RiskCtrlSys.dao.CodeGenerator.PackageConfigGenerator;
import com.juege.RiskCtrlSys.dao.CodeGenerator.StrategyConfigGenerator;
import org.apache.commons.lang3.StringUtils;

import java.util.Scanner;

/**
 * author: Juege
 * description: MybatisPlus 代码自动生成器
 * date: 2024
 */

public class MybatisPlusCodeGenerator {
    public static void main(String[] args) {

        /* **********************
         *
         * 注意：
         *
         * 1.
         *
         * mybatis-plus 3.5.1 +以上的版本 使用
         * FastAutoGenerator 生成代码
         *
         * 2.
         * mybatis-plus 代码生成器需要模板引擎：
         * a. velocity
         * b. freemarker、
         * velocity是生成器中默认使用的
         *
         * *********************/

        FastAutoGenerator
                //配置数据源
                .create(
                        ConstantsUtil.MYSQL_URL,
                        ConstantsUtil.USERNAME,
                        ConstantsUtil.PASSWORD
                )
                // 全局配置
                .globalConfig(new GlobalConfigGenerator())
                // 包配置
                .packageConfig(new PackageConfigGenerator())
                // 策略配置
                .strategyConfig(new StrategyConfigGenerator(scanner("需要逆向的表名")))

                // 使用Freemarker引擎模板(需要导包)
                //.templateEngine(new FreemarkerTemplateEngine())
                // 执行
                .execute();

    }

    /**
     * author: Juege
     * description: 输入需要逆向的表名
     * @param tip: 提示语
     * @return java.lang.String
     */
    private static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder helper = new StringBuilder();
        helper.append(tip); // 添加提示语
        System.out.println(helper.toString()); // 输出提示语
        if (scanner.hasNext()) {
            String table = scanner.next(); // 输入表名
            if (StringUtils.isNotBlank(table)) {
                return table; // 返回输入的表名
            }
        }

        throw new RuntimeException("请输入正确的 " + tip); // 抛出异常，要求输入正确的提示语
    }


}
