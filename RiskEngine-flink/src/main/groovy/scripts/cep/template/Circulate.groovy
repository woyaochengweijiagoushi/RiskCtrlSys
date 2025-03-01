package scripts.cep.template

import com.juege.RiskCtrlSys.flink.job.groovy.LoginFailBySingletonCondition
import com.juege.RiskCtrlSys.flink.job.groovy.GroovyRule
import org.apache.flink.cep.pattern.Pattern
import org.apache.flink.streaming.api.windowing.time.Time

/**
 * 循环模式Pattern模板
 * @param <EventPO>
 */
class Circulate<EventPO> implements GroovyRule<EventPO> {
    @Override
    Pattern<EventPO, EventPO> getPattern() {
        return Pattern
                .<EventPO>begin("__START__")
                .where(new LoginFailBySingletonCondition("__START_FIELD__","__START_EXP__"))
                .times(Integer.parseInt("__TIMES__"))
                .within(Time.seconds(Integer.parseInt("__WITHIN__")))
    }
}
