package io.cloudflight.jems.server.project.service.report.project.verification.updateProjectReportVerificationClarification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditReportVerification
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationClarification
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.ProjectReportVerificationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectReportVerificationClarification(
    private val verificationPersistence: ProjectReportVerificationPersistence,
    private val reportPersistence: ProjectReportPersistence,
): UpdateProjectReportVerificationClarificationInteractor {

    @CanEditReportVerification
    @Transactional
    @ExceptionWrapper(UpdateProjectReportVerificationClarificationException::class)
    override fun updateClarifications(
        projectId: Long,
        reportId: Long,
        clarifications: List<ProjectReportVerificationClarification>
    ): List<ProjectReportVerificationClarification> {
        val report = reportPersistence.getReportById(projectId = projectId, reportId = reportId)
        validateReportStatus(report)

        return verificationPersistence.updateVerificationClarifications(
            projectId = projectId,
            reportId = reportId,
            clarifications = clarifications.reNumber()
        )
    }

    private fun List<ProjectReportVerificationClarification>.reNumber(): List<ProjectReportVerificationClarification> {
        this.forEachIndexed { index, clarification ->
            validateClarification(clarification)
            clarification.number = index.plus(1)
        }

        return this
    }

    private fun validateClarification(clarification: ProjectReportVerificationClarification) {
        if (clarification.comment.isNotEmpty() && clarification.comment.length > 3000)
            throw ReportVerificationInvalidInputException()
    }

    private fun validateReportStatus(report: ProjectReportModel) {
        if (report.status != ProjectReportStatus.InVerification)
            throw ReportVerificationStatusNotValidException()
    }
}
