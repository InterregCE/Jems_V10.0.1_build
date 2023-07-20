package io.cloudflight.jems.server.project.service.report.project.verification.getProjectReportVerificationClarification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewReportVerification
import io.cloudflight.jems.server.project.repository.report.project.verification.ProjectReportVerificationPersistenceProvider
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus.Finalized
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus.InVerification
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationClarification
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectReportVerificationClarification(
    private val projectReportVerificationPersistenceProvider: ProjectReportVerificationPersistenceProvider,
    private val reportPersistence: ProjectReportPersistence,
): GetProjectReportVerificationClarificationInteractor {

    @CanViewReportVerification
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportVerificationClarificationException::class)
    override fun getClarifications(projectId: Long, reportId: Long): List<ProjectReportVerificationClarification> {
        val report = reportPersistence.getReportById(projectId = projectId, reportId = reportId)
        validateReportStatus(report)
        return projectReportVerificationPersistenceProvider.getVerificationClarifications(reportId)
    }


    private fun validateReportStatus(report: ProjectReportModel) {
        if (report.status != InVerification && report.status != Finalized) {
            throw ReportVerificationStatusNotValidException()
        }
    }
}