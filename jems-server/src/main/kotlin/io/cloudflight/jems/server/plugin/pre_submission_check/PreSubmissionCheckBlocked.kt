package io.cloudflight.jems.server.plugin.pre_submission_check

import io.cloudflight.jems.plugin.contract.models.common.I18nMessageData
import io.cloudflight.jems.plugin.contract.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.MessageType
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckMessage
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult
import org.springframework.stereotype.Component

@Component
class PreSubmissionCheckBlocked : PreConditionCheckPlugin {

    override fun check(projectId: Long): PreConditionCheckResult =
        PreConditionCheckResult(listOf(
            PreConditionCheckMessage(I18nMessageData("jems.pre.condition.check.blocked.warning"), MessageType.WARNING, emptyList())
        ) , false)

    override fun getDescription(): String =
        "It will always return false as a result of check"

    override fun getName(): String =
        "Blocked"

    override fun getVersion(): String =
        ""

    override fun getKey(): String  =
        "jems-pre-condition-check-blocked"
}
