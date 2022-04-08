package io.cloudflight.jems.server.project.service.report.partner.expenditure.updateProjectPartnerReportExpenditure

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.clearConversions
import io.cloudflight.jems.server.project.service.report.partner.expenditure.filterInvalidCurrencies
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class UpdateProjectPartnerReportExpenditure(
    private val reportPersistence: ProjectReportPersistence,
    private val reportExpenditurePersistence: ProjectReportExpenditurePersistence,
    private val reportProcurementPersistence: ProjectReportProcurementPersistence,
    private val workPackagePersistence: WorkPackagePersistence,
    private val partnerPersistence: PartnerPersistence,
    private val generalValidator: GeneralValidatorService
) : UpdateProjectPartnerReportExpenditureInteractor {

    companion object {
        private val MAX_NUMBER = BigDecimal.valueOf(999_999_999_99, 2)
        private val MIN_NUMBER = BigDecimal.ZERO

        private const val MAX_AMOUNT = 150
    }

    @CanEditPartnerReport
    @ExceptionWrapper(UpdateProjectPartnerReportExpenditureException::class)
    override fun updatePartnerReportExpenditureCosts(
        partnerId: Long,
        reportId: Long,
        expenditureCosts: List<ProjectPartnerReportExpenditureCost>,
    ): List<ProjectPartnerReportExpenditureCost> {
        validateInputs(expenditureCosts = expenditureCosts)

        val report = reportPersistence.getPartnerReportById(partnerId, reportId = reportId)
        validateReportNotClosed(status = report.status)
        validateCurrencies(expenditureCosts, defaultCurrency = report.identification.currency)

        validateLinkedProcurements(
            expenditureCosts = expenditureCosts,
            allowedProcurementIds = getAvailableProcurements(partnerId, reportId = report.id).mapTo(HashSet()) { it.id },
            allowedInvestmentIds = workPackagePersistence.getProjectInvestmentSummaries(
                projectId = partnerPersistence.getProjectIdForPartnerId(partnerId, report.version),
                version = report.version,
            ).mapTo(HashSet()) { it.id },
        )

        return reportExpenditurePersistence.updatePartnerReportExpenditureCosts(
            partnerId = partnerId,
            reportId = report.id,
            expenditureCosts = expenditureCosts.clearConversions(),
        )
    }

    private fun validateReportNotClosed(status: ReportStatus) {
        if (status.isClosed())
            throw ReportAlreadyClosed()
    }

    private fun validateCurrencies(expenditureCosts: List<ProjectPartnerReportExpenditureCost>, defaultCurrency: String?) {
        val invalidCurrencies = expenditureCosts.filterInvalidCurrencies(defaultCurrency) { it.currencyCode }
        if (invalidCurrencies.isNotEmpty())
            throw PartnerWithDefaultEurCannotSelectOtherCurrency(invalidCurrencies)
    }

    private fun validateInputs(expenditureCosts: List<ProjectPartnerReportExpenditureCost>) {
        if (expenditureCosts.size > MAX_AMOUNT)
            throw MaxAmountOfExpendituresReached(maxAmount = MAX_AMOUNT)

        generalValidator.throwIfAnyIsInvalid(
            *expenditureCosts.mapIndexed { index, it ->
                generalValidator.maxLength(it.description, 255, "description[$index]")
            }.toTypedArray(),
            *expenditureCosts.mapIndexed { index, it ->
                generalValidator.maxLength(it.comment, 255, "comment[$index]")
            }.toTypedArray(),
            *expenditureCosts.mapIndexed { index, it ->
                generalValidator.maxLength(it.internalReferenceNumber, 30, "internalReferenceNumber[$index]")
            }.toTypedArray(),
            *expenditureCosts.mapIndexed { index, it ->
                generalValidator.maxLength(it.invoiceNumber, 30, "invoiceNumber[$index]")
            }.toTypedArray(),
            *expenditureCosts.mapIndexed { index, it ->
                generalValidator.numberBetween(it.declaredAmount, MAX_NUMBER, MIN_NUMBER, "declaredAmount[$index]")
            }.toTypedArray(),
            generalValidator.onlyValidCurrencies(expenditureCosts.mapTo(HashSet()) { it.currencyCode }, "currencyCode"),
        )
    }

    private fun validateLinkedProcurements(
        expenditureCosts: List<ProjectPartnerReportExpenditureCost>,
        allowedProcurementIds: Set<Long>,
        allowedInvestmentIds: Set<Long>,
    ) {
        expenditureCosts.forEach {
            it.contractId = if (it.contractId in allowedProcurementIds) it.contractId else null
            it.investmentId = if (it.investmentId in allowedInvestmentIds) it.investmentId else null
        }
    }

    private fun getAvailableProcurements(partnerId: Long, reportId: Long) =
        reportProcurementPersistence.getProcurementsForReportIds(
            reportIds = reportPersistence.getReportIdsBefore(partnerId = partnerId, beforeReportId = reportId).plus(reportId),
        )
}
