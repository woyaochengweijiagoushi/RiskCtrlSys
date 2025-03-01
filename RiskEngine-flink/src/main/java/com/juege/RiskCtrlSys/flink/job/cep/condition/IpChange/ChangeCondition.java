package com.juege.RiskCtrlSys.flink.job.cep.condition.IpChange;

import com.juege.RiskCtrlSys.model.EventPO;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;

import java.util.Iterator;

/**
 * author: Juege
 * description: 基于迭代条件检测行为事件的IP地址是否更换
 * date: 2024
 */

public class ChangeCondition extends IterativeCondition<EventPO> {
    @Override
    public boolean filter(EventPO eventPO, Context<EventPO> context) throws Exception {

        boolean change = false;

        /* **********************
         *
         * 判断行为事件IP是否更换的思路：
         *
         * 当前行为事件,
         * 和之前的行为事件,
         * 进行IP地址的对比
         *
         * 判断行为事件IP是否更换的关键点：
         * 获取 当前行为事件 之前的行为事件
         *
         * *********************/



        /* **********************
         *
         * 知识点：
         *
         * 1.
         * Context 是上下文对象
         * Context 的getEventsForPattern(模式名),
         * 可以拿到传入的模式名对应模式中已匹配的所有行为事件
         *
         * *********************/

        // 当前模式名称是"ip", 获取当前模式之前已经匹配的事件
        Iterator<EventPO> it = context.getEventsForPattern("ip").iterator();

        //遍历
        while (it.hasNext()){
            //前一个行为事件
            EventPO preEvent = (EventPO) it.next();
            //前一个行为事件的IP
            String preIP = preEvent.getEvent_context().getDevice().getIp();
            //当前行为事件的IP
            String IP = eventPO.getEvent_context().getDevice().getIp();

            //判断前后行为事件的IP是否发生变化
            if(preIP != IP) {
                change = true;
            }
        }

        return change;
    }
}
