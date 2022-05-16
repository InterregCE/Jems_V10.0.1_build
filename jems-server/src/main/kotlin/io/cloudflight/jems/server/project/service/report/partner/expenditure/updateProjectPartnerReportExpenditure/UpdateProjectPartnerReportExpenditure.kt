package io.cloudflight.jems.server.project.service.report.partner.expenditure.updateProjectPartnerReportExpenditure

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.clearConversions
import io.cloudflight.jems.server.project.service.report.partner.expenditure.fillInLumpSum
import io.cloudflight.jems.server.project.service.report.partner.expenditure.fillInUnitCost
import io.cloudflight.jems.server.project.service.report.partner.expenditure.filterInvalidCurrencies
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerReportExpenditureException::class)
    override fun updatePartnerReportExpenditureCosts(
        partnerId: Long,
        reportId: Long,
        expenditureCosts: List<ProjectPartnerReportExpenditureCost>,
    ): List<ProjectPartnerReportExpenditureCost> {
        validateInputs(expenditureCosts = expenditureCosts)

        val report = reportPersistence.getPartnerReportById(partnerId, reportId = reportId)
        validateReportNotClosed(status = report.status)
        validateCostCategories(expenditureCosts)
        validateCurrencies(expenditureCosts, defaultCurrency = report.identification.currency)

        validateLinkedProcurements(
            expenditureCosts = expenditureCosts,
            allowedProcurementIds = getAvailableProcurements(partnerId, reportId = report.id).mapTo(HashSet()) { it.id },
            allowedInvestmentIds = workPackagePersistence.getProjectInvestmentSummaries(
                projectId = partnerPersistence.getProjectIdForPartnerId(partnerId, report.version),
                version = report.version,
            ).mapTo(HashSet()) { it.id },
        )

        validateCostOptions(
            expenditureCosts = expenditureCosts,
            allowedLumpSums = reportExpenditurePersistence.getAvailableLumpSums(partnerId, reportId = reportId),
            allowedUnitCosts = emptyList(),
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

    private fun validateCostCategories(expenditureCosts: List<ProjectPartnerReportExpenditureCost>) {
        expenditureCosts.forEach {
            if (it.costCategory == ReportBudgetCategory.StaffCosts || it.costCategory == ReportBudgetCategory.TravelAndAccommodationCosts)
                it.investmentId = null

            if (it.costCategory == ReportBudgetCategory.StaffCosts) {
                it.contractId = null
                it.vat = null
                it.invoiceNumber = null
            }
        }
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
                generalValidator.numberBetween(it.declaredAmount, MIN_NUMBER, MAX_NUMBER, "declaredAmount[$index]")
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

    private fun validateCostOptions(
        expenditureCosts: List<ProjectPartnerReportExpenditureCost>,
        allowedLumpSums: List<ProjectPartnerReportLumpSum>,
        allowedUnitCosts: List<ProgrammeUnitCost>,
    ) {
        val lumpSumsById = allowedLumpSums.associateBy { it.id }
        val unitCostsById = allowedUnitCosts.associateBy { it.id }
        expenditureCosts.forEach {
            if (it.lumpSumId in lumpSumsById.keys) {
                it.fillInLumpSum(lumpSum = lumpSumsById[it.lumpSumId]!!)
            } else if (it.unitCostId in unitCostsById.keys) {
                it.fillInUnitCost(unitCost = unitCostsById[it.unitCostId]!!)
            } else {
                it.numberOfUnits = BigDecimal.ZERO
                it.pricePerUnit = BigDecimal.ZERO
            }
        }
    }

    private fun getAvailableProcurements(partnerId: Long, reportId: Long) =
        reportProcurementPersistence.getProcurementsForReportIds(
            reportIds = reportPersistence.getReportIdsBefore(partnerId = partnerId, beforeReportId = reportId).plus(reportId),
        )

}
