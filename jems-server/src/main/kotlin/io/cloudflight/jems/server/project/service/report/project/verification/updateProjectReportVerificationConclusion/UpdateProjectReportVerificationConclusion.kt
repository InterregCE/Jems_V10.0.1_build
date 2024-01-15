package io.cloudflight.jems.server.project.service.report.project.verification.updateProjectReportVerificationConclusion

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditReportVerificationPrivileged
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationConclusion
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.ProjectReportVerificationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectReportVerificationConclusion(
    private val verificationPersistence: ProjectReportVerificationPersistence,
    private val reportPersistence: ProjectReportPersistence,
): UpdateProjectReportVerificationConclusionInteractor {

    @CanEditReportVerificationPrivileged
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
        return verificationPersistence.updateVerificationConclusion(
            projectId = projectId,
            reportId = reportId,
            projectReportVerificationConclusion = conclusion
        )
    }

    private fun validateConclusion(conclusion: ProjectReportVerificationConclusion) {
        val conclusionJsLength = conclusion.conclusionJS?.length ?: 0
        val conclusionMaLength = conclusion.conclusionMA?.length ?: 0
        val verificationFollowUpLength = conclusion.verificationFollowUp?.length ?: 0

        if (conclusionJsLength > 5000 || conclusionMaLength > 5000 || verificationFollowUpLength > 5000) {
            throw ReportVerificationInvalidInputException()
        }
    }

    private fun validateReportStatus(report: ProjectReportModel) {
        if (!report.status.canBeVerified()) {
            throw ReportVerificationStatusNotValidException()
        }
    }
}
