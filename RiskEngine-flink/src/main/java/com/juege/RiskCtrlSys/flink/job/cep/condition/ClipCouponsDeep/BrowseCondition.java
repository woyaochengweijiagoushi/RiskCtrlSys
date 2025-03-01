package com.juege.RiskCtrlSys.flink.job.cep.condition.ClipCouponsDeep;

import com.juege.RiskCtrlSys.model.EventPO;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;

/**
 * author: Juege
 * description: TODO
 * date: 2024
 */

public class BrowseCondition extends SimpleCondition<EventPO> {

    @Override
    public boolean filter(EventPO eventPO) throws Exception {

        if (!eventPO.getEvent_context().getProfile().getGrade().equals("L1")) {
            return true;
        }
        return false;
    }
}