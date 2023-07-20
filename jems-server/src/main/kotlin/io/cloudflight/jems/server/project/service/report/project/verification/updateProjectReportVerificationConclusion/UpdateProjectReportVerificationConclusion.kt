package io.cloudflight.jems.server.project.service.report.project.verification.updateProjectReportVerificationConclusion

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditReportVerification
import io.cloudflight.jems.server.project.repository.report.project.verification.ProjectReportVerificationPersistenceProvider
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationConclusion
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectReportVerificationConclusion(
    private val projectReportVerificationPersistenceProvider: ProjectReportVerificationPersistenceProvider,
    private val reportPersistence: ProjectReportPersistence,
): UpdateProjectReportVerificationConclusionInteractor {

    @CanEditReportVerification
    @Transactional
    @ExceptionWrapper(UpdateProjectReportVerificationConclusionException::class)
    override fun updateVerificationConclusion(
        projectId: Long,
        reportId: Long,
        conclusion: ProjectReportVerificationConclusion
    ): ProjectReportVerificationConclusion {
        val report = reportPersistence.getReportById(projectId = projectId, reportId = reportId)
        validateReportStatus(report)
        validateConclusion(conclusion)
        return projectReportVerificationPersistenceProvider.updateProjectReportVerificationConclusion(
            projectId = projectId,
            reportId = reportId,
            projectReportVerificationConclusion = conclusion
        )
    }

    private fun validateConclusion(conclusion: ProjectReportVerificationConclusion) {
        if (conclusion.conclusionJS?.length!! > 5000 ||
            conclusion.conclusionMA?.length!! > 5000 ||
            conclusion.verificationFollowUp?.length!! > 5000) {
            throw ReportVerificationInvalidInputException()
        }
    }

    private fun validateReportStatus(report: ProjectReportModel) {
        if (report.status != ProjectReportStatus.InVerification && report.status != ProjectReportStatus.Finalized) {
            throw ReportVerificationStatusNotValidException()
        }
    }
}
