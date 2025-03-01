package com.juege.RiskCtrlSys.model;

import lombok.Data;

/**
 * author: Juege
 * description: 用户设备POJO对象
 * date: 2024
 */

@Data
public class DevicePO {

    /**
     * ip
     */
    private String ip;
    /**
     * 手机卡唯一标识 imsi
     */
    private String imsi;
    /**
     * 手机唯一标识 imei
     */
    private String imei;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 行政区
     */
    private String area;
}
