package scripts.cep

import com.juege.RiskCtrlSys.flink.job.groovy.GroovyRule
import com.juege.RiskCtrlSys.flink.job.groovy.LoginFailBySingletonCondition
import com.juege.RiskCtrlSys.flink.utils.EventConstantUtil
import com.juege.RiskCtrlSys.model.EventPO
import org.apache.flink.cep.pattern.Pattern
import org.apache.flink.cep.pattern.conditions.SimpleCondition
import org.apache.flink.streaming.api.windowing.time.Time

/**
 * 基于个体模式检测最近1分钟内登录失败超过3次的用户 Groovy脚本
 * @param <EventPO>
 */
class LoginFailBySingleton<EventPO> implements GroovyRule<EventPO> {
    @Override
    Pattern getPattern() {
        return Pattern
                .begin("login_fail_first")
                .where(new LoginFailBySingletonCondition("__FIELD__","__EXP__"))
                .times(3)
                .within(Time.seconds(60));
    }
}
