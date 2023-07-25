package io.cloudflight.jems.server.project.service.report.project.verification.getProjectReportVerificationConclusion

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewReportVerification
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.verification.ProjectReportVerificationConclusion
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.ProjectReportVerificationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectReportVerificationConclusion(
    private val verificationPersistence: ProjectReportVerificationPersistence,
    private val reportPersistence: ProjectReportPersistence,
) : GetProjectReportVerificationConclusionInteractor {

    @CanViewReportVerification
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportVerificationConclusionException::class)
    override fun getVerificationConclusion(projectId: Long, reportId: Long): ProjectReportVerificationConclusion {
        val report = reportPersistence.getReportById(projectId = projectId, reportId = reportId)
        validateReportStatus(report)
        return verificationPersistence.getProjectReportVerificationConclusion(
            projectId = projectId,
            reportId = reportId
        )
    }

    private fun validateReportStatus(report: ProjectReportModel) {
        if (report.status != ProjectReportStatus.InVerification && report.status != ProjectReportStatus.Finalized) {
            throw ReportVerificationStatusNotValidException()
        }
    }
}
