package com.juege.RiskCtrlSys.dao.CodeGenerator;

import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.juege.RiskCtrlSys.commons.constants.ConstantsUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * author: Juege
 * description: MybatisPlus 代码自动生成器 包配置
 * date: 2024
 */

public class PackageConfigGenerator implements Consumer<PackageConfig.Builder> {
    /**
     * 接受并设置包配置器，设置包配置器的各个属性
     *
     * @param builder 包配置器构建器
     */
    @Override
    public void accept(PackageConfig.Builder builder) {
        builder
                .parent(ConstantsUtil.PARENT_PACKAGE) // 设置父包
                .controller(ConstantsUtil.CONTROLLER_PACKAGE) // 设置控制器包
                .mapper(ConstantsUtil.MAPPER_PACKAGE) // 设置映射器包
                .service(ConstantsUtil.SERVICE_PACKAGE) // 设置服务包
                .serviceImpl(ConstantsUtil.SERVICE_IMPL_PACKAGE) // 设置服务实现包
                .entity(ConstantsUtil.ENTITY_PACKAGE) // 设置实体包
                .xml(ConstantsUtil.MAPPER_XML_PACKAGE) // 设置映射器XML包

                .pathInfo(getPathInfo()) // 设置路径信息 (绝对路径)
        ;
    }



    /**
     * 获取路径信息
     *
     * @return 包含OutputFile和对应路径的Map
     */
    private Map<OutputFile, String> getPathInfo() {
        Map<OutputFile, String> pathInfo = new HashMap<>(5);
        pathInfo.put(OutputFile.entity, ConstantsUtil.ENTITY_PATH);
        pathInfo.put(OutputFile.mapper, ConstantsUtil.MAPPER_PATH);
        pathInfo.put(OutputFile.xml, ConstantsUtil.XML_PATH);
        pathInfo.put(OutputFile.controller, ConstantsUtil.CONTROLLER_PATH);
        pathInfo.put(OutputFile.service, ConstantsUtil.SERVICE_PATH);
        pathInfo.put(OutputFile.serviceImpl, ConstantsUtil.SERVICE_IMPL_PATH);

        return pathInfo;
    }


}
