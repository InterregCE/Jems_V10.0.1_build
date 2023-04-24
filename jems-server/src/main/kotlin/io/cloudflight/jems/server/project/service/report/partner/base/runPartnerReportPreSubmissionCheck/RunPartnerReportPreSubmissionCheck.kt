package io.cloudflight.jems.server.project.service.report.partner.base.runPartnerReportPreSubmissionCheck

import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RunPartnerReportPreSubmissionCheck(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val service: RunPartnerReportPreSubmissionCheckService,
) : RunPartnerReportPreSubmissionCheckInteractor {

    @CanEditPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(RunPartnerReportPreSubmissionCheckException::class)
    override fun preCheck(partnerId: Long, reportId: Long): PreConditionCheckResult {
        if (!reportPersistence.exists(partnerId, reportId = reportId)) {
            throw ReportNotFound()
        }
        return service.preCheck(partnerId = partnerId, reportId = reportId)
    }

}
