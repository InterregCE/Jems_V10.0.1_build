package io.cloudflight.jems.server.project.service.report.partner.base.deleteProjectPartnerReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.partnerReportDeleted
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteProjectPartnerReport(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val partnerPersistence: PartnerPersistence,
): DeleteProjectPartnerReportInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(DeleteProjectPartnerReportException::class)
    override fun delete(partnerId: Long, reportId: Long) {
        val latestReport = reportPersistence.getCurrentLatestReportForPartner(partnerId)
            ?: throw ThereIsNoAnyReportForPartner()

        if (latestReport.status.isClosed() || latestReport.id != reportId) {
            throw OnlyLastOpenReportCanBeDeleted(lastOpenReport = latestReport)
        }

        val projectId = partnerPersistence.getProjectIdForPartnerId(id = partnerId)

        reportPersistence.deletePartnerReportById(reportId)
        auditPublisher.publishEvent(partnerReportDeleted(context = this, projectId = projectId, latestReport))
    }
}
