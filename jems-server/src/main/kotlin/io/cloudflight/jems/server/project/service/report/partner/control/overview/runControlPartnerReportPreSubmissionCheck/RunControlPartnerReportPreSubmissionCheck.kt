package io.cloudflight.jems.server.project.service.report.partner.control.overview.runControlPartnerReportPreSubmissionCheck

import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReport
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.base.runPartnerReportPreSubmissionCheck.ReportNotFound
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RunControlPartnerReportPreSubmissionCheck(
    private val projectReportPersistence: ProjectPartnerReportPersistence,
    private val service: RunControlPartnerReportPreSubmissionCheckService
) : RunControlPartnerReportPreSubmissionCheckInteractor {

    @CanEditPartnerControlReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(RunControlPartnerReportPreSubmissionCheckException::class)
    override fun preCheck(partnerId: Long, reportId: Long): PreConditionCheckResult {
        if (!projectReportPersistence.exists(partnerId, reportId = reportId)) {
            throw ReportNotFound()
        }
        return service.preCheck(partnerId = partnerId, reportId = reportId)
    }
}

