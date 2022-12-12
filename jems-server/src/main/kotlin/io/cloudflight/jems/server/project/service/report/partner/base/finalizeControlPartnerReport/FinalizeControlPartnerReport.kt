package io.cloudflight.jems.server.project.service.report.partner.base.finalizeControlPartnerReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.partnerReportControlFinalized
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class FinalizeControlPartnerReport(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val auditPublisher: ApplicationEventPublisher
) : FinalizeControlPartnerReportInteractor {

    @CanEditPartnerControlReport
    @Transactional
    @ExceptionWrapper(FinalizeControlPartnerReportException::class)
    override fun finalizeControl(partnerId: Long, reportId: Long): ReportStatus {
        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId)
        validateReportIsInControl(report)

        return reportPersistence.finalizeControlOnReportById(
            partnerId = partnerId,
            reportId = reportId,
            controlEnd = ZonedDateTime.now(),
        ).also {
            val projectId = partnerPersistence.getProjectIdForPartnerId(id = partnerId, it.version)
            auditPublisher.publishEvent(
                partnerReportControlFinalized(context = this, projectId = projectId, report = it)
            )
        }.status
    }

    private fun validateReportIsInControl(report: ProjectPartnerReport) {
        if (report.status.controlNotOpenAnymore())
            throw ReportNotInControl()
    }

}
