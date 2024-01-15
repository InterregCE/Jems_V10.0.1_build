package io.cloudflight.jems.server.project.service.report.project.base.reOpenVerificationProjectReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.notification.handler.ProjectReportStatusChanged
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.project.authorization.CanReOpenVerificationProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.projectReportVerificationReOpenedAudit
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class ReOpenVerificationProjectReport(
    private val reportPersistence: ProjectReportPersistence,
    private val paymentPersistence: PaymentPersistence,
    private val paymentApplicationToEcLinkPersistence: PaymentApplicationToEcLinkPersistence,
    private val eventPublisher: ApplicationEventPublisher,
): ReOpenVerificationProjectReportInteractor {

    @CanReOpenVerificationProjectReport
    @Transactional
    @ExceptionWrapper(ReOpenVerificationProjectReportException::class)
    override fun reOpen(projectId: Long, reportId: Long): ProjectReportStatus {
        val reportToBeReOpen = reportPersistence.getReportById(projectId = projectId, reportId = reportId)

        validateReportCanBeReOpened(reportToBeReOpen)
        paymentPersistence.deleteRegularPayments(projectReportId = reportId)

        return reportPersistence.reOpenReportTo(reportId = reportId, ProjectReportStatus.ReOpenFinalized, ZonedDateTime.now()).also {
            eventPublisher.publishEvent(ProjectReportStatusChanged(this, it, reportToBeReOpen.status))
            eventPublisher.publishEvent(projectReportVerificationReOpenedAudit(this, it))
        }.status
    }

    private fun validateReportCanBeReOpened(report: ProjectReportModel) {
        if (!report.status.isFinalized())
            throw VerificationReportNotFinalized()

        if (paymentPersistence.getPaymentIdsInstallmentsExistsByProjectReportId(report.id).isNotEmpty())
            throw VerificationReportHasPaymentInstallments()

        if (paymentApplicationToEcLinkPersistence.getPaymentToEcIdsProjectReportIncluded(report.id).isNotEmpty())
            throw VerificationReportIncludedInPaymentToEc()
    }
}
