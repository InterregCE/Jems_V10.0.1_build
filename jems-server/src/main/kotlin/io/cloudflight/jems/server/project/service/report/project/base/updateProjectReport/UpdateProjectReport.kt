package io.cloudflight.jems.server.project.service.report.project.base.updateProjectReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.contracting.reporting.ContractingReportingPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.closurePeriod
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportUpdate
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportDeadline
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.base.toServiceModel
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectReport(
    private val reportPersistence: ProjectReportPersistence,
    private val projectPersistence: ProjectPersistence,
    private val deadlinePersistence: ContractingReportingPersistence,
    private val certificatePersistence: ProjectReportCertificatePersistence,
    private val paymentPersistence: PaymentPersistence,
    private val paymentApplicationToEcLinkPersistence: PaymentApplicationToEcLinkPersistence
) : UpdateProjectReportInteractor {

    companion object {

        private fun ProjectReportUpdate.deadlineManual() = ProjectReportDeadline(
            deadlineId = null,
            type = type,
            periodNumber = periodNumber,
            reportingDate = reportingDate,
            finalReport = finalReport
        )

        fun ProjectReportUpdate.validateInput(
            validPeriodNumbers: Set<Int>,
            datesInvalidExceptionResolver: () -> Exception,
            linkToDeadlineWithManualDataExceptionResolver: () -> Exception,
            noLinkAndDataMissingExceptionResolver: () -> Exception,
            periodNumberExceptionResolver: (Int) -> Exception,
        ) {
            validateDates(this, datesInvalidExceptionResolver)
            when {
                this.isLink() -> validateLink(this, linkToDeadlineWithManualDataExceptionResolver)
                this.isManual() -> validateManual(data = this, validPeriodNumbers = validPeriodNumbers,
                    noLinkAndDataMissingExceptionResolver = noLinkAndDataMissingExceptionResolver,
                    periodNumberExceptionResolver = periodNumberExceptionResolver)
            }
        }

        private fun validateDates(data: ProjectReportUpdate, datesInvalidExceptionResolver: () -> Exception) {
            if (data.startDate != null && data.endDate != null && data.startDate.isAfter(data.endDate))
                throw datesInvalidExceptionResolver.invoke()
        }

        private fun validateLink(data: ProjectReportUpdate, linkToDeadlineWithManualDataExceptionResolver: () -> Exception) {
            if (data.containsManualValues())
                throw linkToDeadlineWithManualDataExceptionResolver.invoke()
        }

        private fun validateManual(
            data: ProjectReportUpdate,
            validPeriodNumbers: Set<Int>,
            noLinkAndDataMissingExceptionResolver: () -> Exception,
            periodNumberExceptionResolver: (Int) -> Exception,
        ) {
            if (data.isMissingManualValues())
                throw noLinkAndDataMissingExceptionResolver.invoke()

            if (data.periodNumber!! !in validPeriodNumbers)
                throw periodNumberExceptionResolver.invoke(data.periodNumber)
        }

        private fun ProjectReportUpdate.containsManualValues() =
            type != null || periodNumber != null || reportingDate != null

        private fun ProjectReportUpdate.isMissingManualValues() =
            type == null || periodNumber == null || reportingDate == null

        private fun ProjectReportUpdate.isLink() = deadlineId != null

        private fun ProjectReportUpdate.isManual() = !isLink()

        private fun ProjectReportUpdate.toDeadlineObject() =
            if (isManual())
                deadlineManual()
            else
                ProjectReportDeadline(
                    deadlineId = deadlineId!!,
                    type = null,
                    periodNumber = null,
                    reportingDate = null,
                    finalReport = null
                )

    }

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(UpdateProjectReportException::class)
    override fun updateReport(projectId: Long, reportId: Long, data: ProjectReportUpdate): ProjectReport {
        val report = reportPersistence.getReportById(projectId, reportId)
        val version = report.linkedFormVersion
        val periods = projectPersistence.getProjectPeriods(projectId, version).plus(closurePeriod).associateBy { it.number }

        data.validateInput(validPeriodNumbers = periods.keys,
            datesInvalidExceptionResolver = { StartDateIsAfterEndDate() },
            linkToDeadlineWithManualDataExceptionResolver = { LinkToDeadlineProvidedWithManualDataOverride() },
            noLinkAndDataMissingExceptionResolver = { LinkToDeadlineNotProvidedAndDataMissing() },
            periodNumberExceptionResolver = { PeriodNumberInvalid(it) },
        )

        val oldType = report.type
        val newType = data.deadlineId?.let { deadlinePersistence.getContractingReportingDeadline(projectId, it).type } ?: data.type
        val thereAreChangesToBaseData = oldType != newType || report.deadlineId != data.deadlineId

        if (report.status.hasBeenSubmitted() && thereAreChangesToBaseData)
            throw TypeChangeIsForbiddenWhenReportIsReOpened()

        if (oldType.hasFinance() && newType.doesNotHaveFinance())
            certificatePersistence.deselectCertificatesOfProjectReport(projectReportId = reportId)

        val paymentIdsInstallmentExists = report.getPaymentIdsInstallmentExists()
        val paymentToEcIdsReportIncluded = report.getPaymentToEcIdsReportIncluded()

        return reportPersistence.updateReport(
            projectId = projectId,
            reportId = reportId,
            startDate = data.startDate,
            endDate = data.endDate,
            deadline =  data.toDeadlineObject(),
        ).toServiceModel(
            periodResolver = { periodNumber -> periods[periodNumber]!! },
            paymentIdsInstallmentExists = paymentIdsInstallmentExists,
            paymentToEcIdsReportIncluded = paymentToEcIdsReportIncluded
        )
    }

    private fun ContractingDeadlineType?.hasFinance() = this?.hasFinance() ?: false
    private fun ContractingDeadlineType?.doesNotHaveFinance() = this == ContractingDeadlineType.Content

    private fun ProjectReportModel.getPaymentIdsInstallmentExists() =
        if (this.status.isFinalized()) paymentPersistence.getPaymentIdsInstallmentsExistsByProjectReportId(this.id) else setOf()

    private fun ProjectReportModel.getPaymentToEcIdsReportIncluded() =
        if (this.status.isFinalized()) paymentApplicationToEcLinkPersistence.getPaymentToEcIdsProjectReportIncluded(this.id) else setOf()


}
