package io.cloudflight.jems.server.plugin.pre_submission_check

import io.cloudflight.jems.plugin.contract.models.common.I18nMessageData
import io.cloudflight.jems.plugin.contract.pre_condition_check.ControlReportPartnerCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.MessageType
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckMessage
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult
import org.springframework.stereotype.Component

@Component
class ControlPartnerReportCheckBlocked: ControlReportPartnerCheckPlugin {

    companion object{
        const val KEY = "control-report-partner-check-blocked"
    }

    override fun check(partnerId: Long, reportId: Long): PreConditionCheckResult =
        PreConditionCheckResult(listOf(
            PreConditionCheckMessage(I18nMessageData("jems.control.report.partner.check.blocked.warning"), MessageType.WARNING, emptyList())
        ) , false)

    override fun getDescription() = "It will always return false as a result of check"

    override fun getName() = "Blocked"

    override fun getVersion() = ""

    override fun getKey() = KEY
}
