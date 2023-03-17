package io.cloudflight.jems.server.plugin.pre_submission_check

import io.cloudflight.jems.plugin.contract.pre_condition_check.ControlReportSamplingCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.ControlReportSamplingCheckResult
import org.springframework.stereotype.Component

@Component
class ControlReportSamplingCheckOff: ControlReportSamplingCheckPlugin {

    companion object{
        const val KEY = "control-report-sampling-check-off"
    }

    override fun check(partnerId: Long, reportId: Long): ControlReportSamplingCheckResult =
        ControlReportSamplingCheckResult(setOf())

    override fun getDescription() = "It does not check anything"

    override fun getName() = "No-Check"

    override fun getVersion() = ""

    override fun getKey()  = KEY
}
