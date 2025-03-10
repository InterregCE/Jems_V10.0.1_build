package io.cloudflight.jems.server.project.service.report.project.base.startVerificationProjectReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.notification.handler.ProjectReportStatusChanged
import io.cloudflight.jems.server.project.authorization.CanStartProjectReportVerification
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.projectReportStartedVerification
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StartVerificationProjectReport(
    private val reportPersistence: ProjectReportPersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val projectReportExpenditureVerificationPersistence: ProjectReportVerificationExpenditurePersistence,
) : StartVerificationProjectReportInteractor {

    @CanStartProjectReportVerification
    @Transactional
    @ExceptionWrapper(StartVerificationProjectReportException::class)
    override fun startVerification(projectId: Long, reportId: Long): ProjectReportStatus {
        val report = reportPersistence.getReportById(projectId = projectId, reportId = reportId)
        validateReportIsSubmitted(report)

        projectReportExpenditureVerificationPersistence.initiateEmptyVerificationForProjectReport(reportId)

        return reportPersistence.startVerificationOnReportById(
            projectId = projectId,
            reportId = reportId
        ).also {
            auditPublisher.publishEvent(ProjectReportStatusChanged(this, it, report.status))

            auditPublisher.publishEvent(
                projectReportStartedVerification(
                    context = this,
                    projectId = projectId,
                    report = it,
                )
            )
        }.status
    }

    private fun validateReportIsSubmitted(report: ProjectReportModel) {
        if (report.status != ProjectReportStatus.Submitted)
            throw ReportNotSubmitted()
    }
}
