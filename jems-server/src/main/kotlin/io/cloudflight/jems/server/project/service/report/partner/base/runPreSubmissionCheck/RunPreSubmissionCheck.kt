package io.cloudflight.jems.server.project.service.report.partner.base.runPreSubmissionCheck

import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RunPreSubmissionCheck(
    private val reportPersistence: ProjectReportPersistence,
    private val service: RunPreSubmissionCheckService,
) : RunPreSubmissionCheckInteractor {

    @CanEditPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(RunPreSubmissionCheckException::class)
    override fun preCheck(partnerId: Long, reportId: Long): PreConditionCheckResult {
        if (!reportPersistence.exists(partnerId, reportId = reportId)) {
            throw ReportNotFound()
        }
        return service.preCheck(partnerId = partnerId, reportId = reportId)
    }

}
