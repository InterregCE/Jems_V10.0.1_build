package io.cloudflight.jems.server.project.service.report.partner.base.reOpenProjectPartnerReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.notification.handler.PartnerReportStatusChanged
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus.ReOpenInControlLast
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus.ReOpenInControlLimited
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus.Submitted
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus.ReOpenSubmittedLimited
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus.ReOpenSubmittedLast
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.partnerReportReOpened
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReOpenProjectPartnerReport(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val projectPersistence: ProjectPersistence,
) : ReOpenProjectPartnerReportInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(ReOpenProjectPartnerReportException::class)
    override fun reOpen(partnerId: Long, reportId: Long): ReportStatus {
        val reportToBeReOpen = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId)

        validateReportCanBeReOpened(reportToBeReOpen)

        val isLastReport = reportToBeReOpen.id == reportPersistence.getCurrentLatestReportForPartner(partnerId = partnerId)!!.id
        val status =
            if (reportToBeReOpen.status == Submitted)
                if (isLastReport) ReOpenSubmittedLast else ReOpenSubmittedLimited
            else
                if (isLastReport) ReOpenInControlLast else ReOpenInControlLimited

        return reportPersistence.reOpenReportById(partnerId = partnerId, reportId = reportId, status).also {
            val projectId = partnerPersistence.getProjectIdForPartnerId(id = partnerId, it.version)
            val projectSummary = projectPersistence.getProjectSummary(projectId)

            auditPublisher.publishEvent(PartnerReportStatusChanged(this, projectSummary, it))
            auditPublisher.publishEvent(partnerReportReOpened(this, projectId, it))
        }.status
    }

    private fun validateReportCanBeReOpened(report: ProjectPartnerReport) {
        if (report.status.canNotBeReOpened())
            throw ReportCanNotBeReOpened()
    }

}
