package io.cloudflight.jems.server.project.service.report.partner.base.runPartnerReportPreSubmissionCheck

import io.cloudflight.jems.plugin.contract.pre_condition_check.ReportPartnerCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RunPartnerReportPreSubmissionCheckService(
    private val jemsPluginRegistry: JemsPluginRegistry,
    private val callPersistence: CallPersistence,
) {

    @Transactional(readOnly = true)
    fun preCheck(partnerId: Long, reportId: Long): PreConditionCheckResult {
        val pluginKey = callPersistence.getCallSimpleByPartnerId(partnerId).reportPartnerCheckPluginKey

        val plugin = jemsPluginRegistry.get(ReportPartnerCheckPlugin::class, key = pluginKey)
        return runCatching { plugin.check(partnerId, reportId = reportId) }
            .getOrElse { throw PreCheckPluginFailed(it) }
    }

}
