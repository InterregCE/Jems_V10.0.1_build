package io.cloudflight.jems.server.project.service.report.partner.base.startControlPartnerReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partnerReportStartedControl
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StartControlPartnerReport(
    private val reportPersistence: ProjectReportPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : StartControlPartnerReportInteractor {

    @PreAuthorize("false")
    @Transactional
    @ExceptionWrapper(StartControlPartnerReportException::class)
    override fun startControl(partnerId: Long, reportId: Long): ReportStatus {
        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId)
        validateReportIsSubmitted(report)

        return reportPersistence.startControlOnReportById(
            partnerId = partnerId,
            reportId = reportId,
        ).also {
            auditPublisher.publishEvent(
                partnerReportStartedControl(
                    context = this,
                    projectId = partnerPersistence.getProjectIdForPartnerId(id = partnerId, it.version),
                    report = it,
                )
            )
        }.status
    }

    private fun validateReportIsSubmitted(report: ProjectPartnerReport) {
        if (report.status != ReportStatus.Submitted)
            throw ReportNotSubmitted()
    }
}

