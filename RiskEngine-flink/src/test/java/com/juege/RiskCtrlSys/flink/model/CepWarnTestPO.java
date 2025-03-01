package com.juege.RiskCtrlSys.flink.model;

import lombok.Data;

import java.util.Iterator;
import java.util.List;

/**
 * author: Juege
 * description: Flink-Cep 告警POJO对象 (用于单元测试)
 * date: 2024
 */

@Data
public class CepWarnTestPO {

    private String name;
    private List<SimpleEventTestPO> events;

    public CepWarnTestPO(List<SimpleEventTestPO> events,String name) {
        this.events = events;
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("规则："+name+" ");
        int i = 0;
        Iterator<SimpleEventTestPO> it = events.iterator();
        while(it.hasNext()) {
            ++i;
            SimpleEventTestPO event = it.next();
            builder
                    .append("事件"+i+" ")
                    .append("uid:")
                    .append(event.getUser_id_int())
                    .append(", 时间:")
                    .append(event.getEvent_time())
                    .append(", 行为:")
                    .append(event.getEvent_name())
                    .append("。")
                    ;
        }

        return builder.toString();
    }
}
