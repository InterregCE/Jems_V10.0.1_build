package io.cloudflight.jems.server.project.service.report.project.base.runProjectReportPreSubmissionCheck

import io.cloudflight.jems.plugin.contract.pre_condition_check.ReportPartnerCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.ReportProjectCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RunProjectReportPreSubmissionCheckService(
    private val jemsPluginRegistry: JemsPluginRegistry,
    private val callPersistence: CallPersistence,
) {

    @Transactional(readOnly = true)
    fun preCheck(projectId: Long, reportId: Long): PreConditionCheckResult {
        val pluginKey = callPersistence.getCallByProjectId(projectId).reportProjectCheckPluginKey

        val plugin = jemsPluginRegistry.get(ReportProjectCheckPlugin::class, key = pluginKey)
        return runCatching { plugin.check(projectId, reportId = reportId) }
            .getOrElse { throw PreCheckPluginFailed(it) }
    }

}
