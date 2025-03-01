package scripts.cep.template

import com.juege.RiskCtrlSys.flink.job.groovy.LoginFailBySingletonCondition
import com.juege.RiskCtrlSys.flink.job.groovy.GroovyRule
import org.apache.flink.cep.pattern.Pattern
import org.apache.flink.streaming.api.windowing.time.Time

/**
 * 组合模式（2个模式）检测连续事件Pattern模板
 * @param <EventPO>
 */
class CompositeBy2<EventPO> implements GroovyRule<EventPO> {
    @Override
    Pattern<EventPO, EventPO> getPattern() {
        return Pattern
                .<EventPO>begin("__START__")
                .where(new LoginFailBySingletonCondition("__START_FIELD__","__START_EXP__"))
                .followedBy("__SECOND__")
                .where(new LoginFailBySingletonCondition("__SEC_FIELD__","__SEC_EXP__"))
                .within(Time.seconds(Integer.parseInt("__WITHIN__")))
    }
}
