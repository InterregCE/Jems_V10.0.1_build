package io.cloudflight.jems.server.project.service.report.project.verification.getProjectReportVerificationClarification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewReportVerificationPrivileged
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationClarification
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.ProjectReportVerificationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectReportVerificationClarification(
    private val verificationPersistence: ProjectReportVerificationPersistence,
    private val reportPersistence: ProjectReportPersistence,
) : GetProjectReportVerificationClarificationInteractor {

    @CanViewReportVerificationPrivileged
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportVerificationClarificationException::class)
    override fun getClarifications(projectId: Long, reportId: Long): List<ProjectReportVerificationClarification> {
        val report = reportPersistence.getReportById(projectId = projectId, reportId = reportId)
        validateReportStatus(report)
        return verificationPersistence.getVerificationClarifications(reportId)
    }

    private fun validateReportStatus(report: ProjectReportModel) {
        if (report.status.verificationNotStartedYet()) {
            throw ReportVerificationStatusNotValidException()
        }
    }

}
