package io.cloudflight.jems.server.project.service.report.project.base.getProjectReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.base.toServiceModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectReport(
    private val reportPersistence: ProjectReportPersistence,
    private val projectPersistence: ProjectPersistence,
    private val paymentPersistence: PaymentPersistence,
    private val paymentApplicationToEcLinkPersistence: PaymentApplicationToEcLinkPersistence
) : GetProjectReportInteractor {

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportException::class)
    override fun findById(projectId: Long, reportId: Long): ProjectReport =
        reportPersistence.getReportById(projectId, reportId = reportId).let { report ->
            val periods = report.getProjectPeriods()
            val paymentIdsInstallmentExists = report.getPaymentIdsInstallmentExists()
            val paymentToEcIdsReportIncluded = report.getPaymentToEcIdsReportIncluded()
            report.toServiceModel(
                periodResolver = { periodNumber -> periods.first { it.number == periodNumber } },
                paymentIdsInstallmentExists,
                paymentToEcIdsReportIncluded
            )
        }

    private fun ProjectReportModel.getProjectPeriods() =
        projectPersistence.getProjectPeriods(projectId, linkedFormVersion)

    private fun ProjectReportModel.getPaymentIdsInstallmentExists() =
        if (this.status.isFinalized()) paymentPersistence.getPaymentIdsInstallmentsExistsByProjectReportId(this.id) else setOf()

    private fun ProjectReportModel.getPaymentToEcIdsReportIncluded() =
        if (this.status.isFinalized()) paymentApplicationToEcLinkPersistence.getPaymentToEcIdsProjectReportIncluded(this.id) else setOf()

}
