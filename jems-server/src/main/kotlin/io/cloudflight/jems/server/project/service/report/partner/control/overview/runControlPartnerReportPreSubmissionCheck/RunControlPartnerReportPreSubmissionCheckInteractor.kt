package io.cloudflight.jems.server.project.service.report.partner.control.overview.runControlPartnerReportPreSubmissionCheck

import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult

interface RunControlPartnerReportPreSubmissionCheckInteractor {
    fun preCheck(partnerId: Long, reportId: Long): PreConditionCheckResult
}
