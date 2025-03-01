package com.juege.RiskCtrlSys.dao.CodeGenerator;


import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.function.Consumer;

/**
 * author: Juege
 * description: MybatisPlus 代码自动生成器 包配置
 * date: 2024
 */

public class StrategyConfigGenerator implements Consumer<StrategyConfig.Builder> {

    private String table;

    public StrategyConfigGenerator(String table) {
        this.table = table;
    }

    @Override
    public void accept(StrategyConfig.Builder builder) {
        builder
                // 设置需要生成的表名
                .addInclude(table)

                // Entity 策略配置
                .entityBuilder()
                .formatFileName("%sEntity")
                //开启 Lombok
                .enableLombok()
                // 覆盖已生成文件
                .enableFileOverride()
                //数据库表映射到实体的命名策略：下划线转驼峰命
                .naming(NamingStrategy.no_change)
                //数据库表字段映射到实体的命名策略：下划线转驼峰命
                .columnNaming(NamingStrategy.no_change)

                // Mapper 策略配置
                .mapperBuilder()
                // 覆盖已生成文件
                .enableFileOverride()

                // Service 策略配置
                .serviceBuilder()
                // 覆盖已生成文件
                .enableFileOverride()
                //格式化 service 接口文件名称
                .formatServiceFileName("%sService")
                //格式化 service 实现类文件名称
                .formatServiceImplFileName("%sServiceImpl")

                // Controller 策略配置
                .controllerBuilder()
                .formatFileName("%sController")
                // 覆盖已生成文件
                .enableFileOverride()
                ;
    }
}
