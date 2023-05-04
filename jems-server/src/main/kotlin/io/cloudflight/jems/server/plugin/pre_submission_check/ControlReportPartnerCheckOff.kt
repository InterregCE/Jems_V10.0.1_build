package io.cloudflight.jems.server.plugin.pre_submission_check

import io.cloudflight.jems.plugin.contract.models.common.I18nMessageData
import io.cloudflight.jems.plugin.contract.pre_condition_check.ControlReportPartnerCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.MessageType
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckMessage
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult
import org.springframework.stereotype.Component

@Component
class ControlReportPartnerCheckOff: ControlReportPartnerCheckPlugin {

    companion object{
        const val KEY = "control-report-partner-check-off"
    }

    override fun check(partnerId: Long, reportId: Long): PreConditionCheckResult =
        PreConditionCheckResult(listOf(
            PreConditionCheckMessage(I18nMessageData("jems.control.report.partner.check.off.info"), MessageType.INFO, emptyList())
        ) , true)

    override fun getDescription() = "It does not check anything"

    override fun getName() = "No-Check"

    override fun getVersion() = ""

    override fun getKey()  = KEY
}
