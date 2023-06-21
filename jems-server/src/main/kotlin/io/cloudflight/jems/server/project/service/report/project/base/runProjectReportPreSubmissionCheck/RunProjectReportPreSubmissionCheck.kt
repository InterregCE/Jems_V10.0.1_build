package io.cloudflight.jems.server.project.service.report.project.base.runProjectReportPreSubmissionCheck

import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RunProjectReportPreSubmissionCheck(
    private val reportPersistence: ProjectReportPersistence,
    private val service: RunProjectReportPreSubmissionCheckService,
) : RunProjectReportPreSubmissionCheckInteractor {

    @CanEditProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(RunProjectReportPreSubmissionCheckException::class)
    override fun preCheck(projectId: Long, reportId: Long): PreConditionCheckResult {
        if (!reportPersistence.exists(projectId, reportId = reportId)) {
            throw ReportNotFound()
        }
        return service.preCheck(projectId = projectId, reportId = reportId)
    }

}
