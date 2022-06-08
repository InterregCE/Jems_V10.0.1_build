package io.cloudflight.jems.server.plugin.pre_submission_check

import io.cloudflight.jems.plugin.contract.models.common.I18nMessageData
import io.cloudflight.jems.plugin.contract.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.MessageType
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckMessage
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult
import org.springframework.stereotype.Component

@Component
class PreSubmissionCheckOff : PreConditionCheckPlugin {

    companion object{
        const val KEY = "jems-pre-condition-check-off"
    }

    override fun check(projectId: Long): PreConditionCheckResult =
        PreConditionCheckResult(listOf(
            PreConditionCheckMessage(I18nMessageData("jems.pre.condition.check.off.info"), MessageType.INFO, emptyList())
        ) , true)

    override fun getDescription(): String =
        "It does not check anything"

    override fun getName(): String =
        "No-Check"

    override fun getVersion(): String =
        ""

    override fun getKey(): String  =
        KEY
}
