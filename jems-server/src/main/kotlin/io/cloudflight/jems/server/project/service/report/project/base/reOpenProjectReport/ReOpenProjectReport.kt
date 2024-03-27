package io.cloudflight.jems.server.project.service.report.project.base.reOpenProjectReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.notification.handler.ProjectReportStatusChanged
import io.cloudflight.jems.server.project.authorization.CanReOpenProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus.InVerification
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus.VerificationReOpenedLast
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus.VerificationReOpenedLimited
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus.ReOpenFinalized
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus.ReOpenSubmittedLast
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus.ReOpenSubmittedLimited
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus.Submitted
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.projectReportReOpenedAudit
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class ReOpenProjectReport(
    private val reportPersistence: ProjectReportPersistence,
    private val eventPublisher: ApplicationEventPublisher,
) : ReOpenProjectReportInteractor {

    @CanReOpenProjectReport
    @Transactional
    @ExceptionWrapper(ReOpenProjectReportException::class)
    override fun reOpen(projectId: Long, reportId: Long): ProjectReportStatus {
        val reportToBeReOpen = reportPersistence.getReportById(projectId = projectId, reportId = reportId)

        validateReportCanBeReOpened(reportToBeReOpen)

        val isLatestReport = reportId == reportPersistence
            .getCurrentLatestReportOfType(reportToBeReOpen.projectId, reportToBeReOpen.type!!)!!.id
        val status = calculateNewStatus(reportToBeReOpen.status, isLatestReport)

        return reportPersistence.reOpenProjectReport(reportId = reportId, status).also {
            eventPublisher.publishEvent(ProjectReportStatusChanged(this, it, reportToBeReOpen.status))
            eventPublisher.publishEvent(projectReportReOpenedAudit(this, it))
        }.status
    }

    private fun validateReportCanBeReOpened(report: ProjectReportModel) {
        if (report.status.canNotBeReOpened())
            throw ReportCanNotBeReOpened()
    }

    private fun calculateNewStatus(oldStatus: ProjectReportStatus, isLatestReport: Boolean) =
        when(oldStatus) {
            Submitted -> if (isLatestReport) ReOpenSubmittedLast else ReOpenSubmittedLimited
            InVerification, ReOpenFinalized -> if (isLatestReport) VerificationReOpenedLast else VerificationReOpenedLimited
            else -> throw ReportCanNotBeReOpened()
        }

}
