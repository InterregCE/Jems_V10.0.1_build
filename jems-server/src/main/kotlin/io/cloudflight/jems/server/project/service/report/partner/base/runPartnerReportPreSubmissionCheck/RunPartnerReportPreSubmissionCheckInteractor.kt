package io.cloudflight.jems.server.project.service.report.partner.base.runPartnerReportPreSubmissionCheck

import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult

interface RunPartnerReportPreSubmissionCheckInteractor {

    fun preCheck(partnerId: Long, reportId: Long): PreConditionCheckResult

}
