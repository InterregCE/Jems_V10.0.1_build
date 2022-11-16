package io.cloudflight.jems.server.project.service.report.partner.base.deleteProjectPartnerReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.partnerReportDeleted
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteProjectPartnerReport(
    private val reportPersistence: ProjectReportPersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val partnerPersistence: PartnerPersistence,
): DeleteProjectPartnerReportInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(DeleteProjectPartnerReportException::class)
    override fun delete(partnerId: Long, reportId: Long) {
        val partnerReport = reportPersistence.getPartnerReportById(partnerId, reportId)
        val latestReportNumber = reportPersistence.getCurrentLatestReportNumberForPartner(partnerId)
        if (partnerReport.status.isClosed() || partnerReport.reportNumber != latestReportNumber) {
            throw DeletionIsNotAllowedException()
        }
        reportPersistence.deletePartnerReportById(reportId)
        auditPublisher.publishEvent(
            partnerReportDeleted(
                this,
                partnerPersistence.getProjectIdForPartnerId(id = partnerId),
                partnerReport)
        )
    }
}
