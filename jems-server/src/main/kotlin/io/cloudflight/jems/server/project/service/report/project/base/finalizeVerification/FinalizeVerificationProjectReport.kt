package io.cloudflight.jems.server.project.service.report.project.base.finalizeVerification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.notification.handler.ProjectReportStatusChanged
import io.cloudflight.jems.server.project.authorization.CanFinalizeProjectReportVerification
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.projectReportFinalizedVerification
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FinalizeVerificationProjectReport(
    private val reportPersistence: ProjectReportPersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val expenditureVerificationPersistence: ProjectReportVerificationExpenditurePersistence
) : FinalizeVerificationProjectReportInteractor {

    @Transactional
    @CanFinalizeProjectReportVerification
    @ExceptionWrapper(FinalizeVerificationProjectReportException::class)
    override fun finalizeVerification(projectId: Long, reportId: Long): ProjectReportStatus {
        val report = reportPersistence.getReportById(projectId = projectId, reportId = reportId)
        validateReportIsInVerification(report)
        val parkedExpenditures = expenditureVerificationPersistence.getParkedProjectReportExpenditureVerification(reportId)

        return reportPersistence.finalizeVerificationOnReportById(projectId = projectId, reportId = reportId).also {
            auditPublisher.publishEvent(ProjectReportStatusChanged(this, it))
            auditPublisher.publishEvent(
                projectReportFinalizedVerification(
                    context = this,
                    projectId = projectId,
                    report = it,
                    parkedExpenditures
                )
            )
        }.status
    }

    private fun validateReportIsInVerification(report: ProjectReportModel) {
        if (report.status != ProjectReportStatus.InVerification)
            throw ReportVerificationNotStartedException()
    }
}
