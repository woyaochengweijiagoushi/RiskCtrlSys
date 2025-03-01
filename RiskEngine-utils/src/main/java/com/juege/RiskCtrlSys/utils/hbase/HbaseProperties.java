package com.juege.RiskCtrlSys.utils.hbase;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * author: Juege
 * description: Hbase配置信息读取类
 * date: 2024
 */

@Data
@ConfigurationProperties(prefix = "hbase.conf")
public class HbaseProperties {

    private Map<String, String> confMaps;
}
