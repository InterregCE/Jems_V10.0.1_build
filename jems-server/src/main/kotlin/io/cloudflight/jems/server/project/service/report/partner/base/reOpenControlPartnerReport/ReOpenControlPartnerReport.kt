package io.cloudflight.jems.server.project.service.report.partner.base.reOpenControlPartnerReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.notification.handler.PartnerReportStatusChanged
import io.cloudflight.jems.server.project.authorization.CanReOpenCertifiedReport
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.partnerReportControlReOpened
import java.time.ZonedDateTime
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReOpenControlPartnerReport(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val projectPartnerRepository: ProjectPartnerRepository,
    private val auditPublisher: ApplicationEventPublisher,
): ReOpenControlPartnerReportInteractor {

    @CanReOpenCertifiedReport
    @Transactional
    @ExceptionWrapper(ReOpenControlPartnerReportException::class)
    override fun reOpen(partnerId: Long, reportId: Long): ReportStatus {

        val reportControlToReOpen = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId)
        val projectId = projectPartnerRepository.getProjectIdForPartner(partnerId) ?: 0L

        validateControlReportStatusForReopening(reportControlToReOpen)
        validateCertificateForReopeningControl(reportControlToReOpen)

        return reportPersistence.updateStatusAndTimes(
            partnerId, reportId, ReportStatus.ReOpenCertified, lastControlReopening = ZonedDateTime.now()
        ).also {
            auditPublisher.publishEvent(PartnerReportStatusChanged(this, projectId, it, reportControlToReOpen.status))
            auditPublisher.publishEvent(partnerReportControlReOpened(this, projectId, it))
        }.status
    }

    private fun validateControlReportStatusForReopening(reportControlToReOpen: ProjectPartnerReport) {
        if (reportControlToReOpen.status != ReportStatus.Certified)
            throw ReportCanNotBeReOpened()
    }

    private fun validateCertificateForReopeningControl(reportControlToReOpen: ProjectPartnerReport) {
        if (reportControlToReOpen.certificateIncludedInProjectReport()) {
            throw ReportCertificateException()
        }
    }

    private fun ProjectPartnerReport.certificateIncludedInProjectReport() =
        this.projectReportId != null

}
