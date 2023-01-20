package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlDeductionOverview

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.typologyerrors.ProgrammeTypologyErrorsPersistence
import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.budget.calculator.BudgetCostCategory
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlDeductionOverview
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlDeductionOverviewRow
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.getCategory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetReportControlDeductionOverview(
    private val typologyOfErrorsPersistence: ProgrammeTypologyErrorsPersistence,
    private val reportExpenditurePersistence: ProjectPartnerReportExpenditureVerificationPersistence,
    private val reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence
) : GetReportControlDeductionOverviewInteractor {

    @CanViewPartnerControlReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetReportControlDeductionOverviewException::class)
    override fun get(partnerId: Long, reportId: Long, linkedFormVersion: String?): ControlDeductionOverview {

        val typologiesOfErrors = this.typologyOfErrorsPersistence.getAllTypologyErrors()
        val expenditureData =
            reportExpenditurePersistence.getPartnerControlReportExpenditureVerification(partnerId, reportId)
                .filter { it.typologyOfErrorId != null }
        val costCategories = reportExpenditureCostCategoryPersistence.getCostCategories(partnerId = partnerId, reportId)
        val byTypologyError = expenditureData
            .filter { it.typologyOfErrorId != null }
            .groupBy { it.typologyOfErrorId!! }
            .mapValues {
                it.value
                    .groupBy { it.getCategory() }
                    .mapValues { it.value.sumOf { it.deductedAmount } }
            }

        val flatRates = expenditureData.calculateTypology(costCategories.options)
        val flatRateSetup = reportExpenditureCostCategoryPersistence.getCostCategories(partnerId = partnerId, reportId).options
        val result = typologiesOfErrors
            .map { error -> byTypologyError.extractFor(error = error) }
            .plus(flatRateSetup.toRow(flatRates))
            .fillInTotal()

        return ControlDeductionOverview(
            deductionRows = result,
            staffCostsFlatRate = flatRateSetup.staffCostsFlatRate,
            officeAndAdministrationFlatRate = flatRateSetup.officeAndAdministrationOnStaffCostsFlatRate
                ?: flatRateSetup.officeAndAdministrationOnDirectCostsFlatRate,
            travelAndAccommodationFlatRate = flatRateSetup.travelAndAccommodationOnStaffCostsFlatRate,
            otherCostsOnStaffCostsFlatRate = flatRateSetup.otherCostsOnStaffCostsFlatRate,
            total = result.sumUp(),
        )
    }

    private fun Map<Long, Map<BudgetCostCategory, BigDecimal>>.getForTypologyAndCategory(typologyId: Long, category: BudgetCostCategory): BigDecimal? =
        get(typologyId)?.get(category) ?: BigDecimal.ZERO

    private fun List<ControlDeductionOverviewRow>.fillInTotal() = this.onEach {
        it.total = (it.staffCost ?: BigDecimal.ZERO)
            .plus(it.officeAndAdministration ?: BigDecimal.ZERO)
            .plus(it.travelAndAccommodation ?: BigDecimal.ZERO)
            .plus(it.externalExpertise ?: BigDecimal.ZERO)
            .plus(it.equipment ?: BigDecimal.ZERO)
            .plus(it.infrastructureAndWorks ?: BigDecimal.ZERO)
            .plus(it.lumpSums ?: BigDecimal.ZERO)
            .plus(it.unitCosts ?: BigDecimal.ZERO)
            .plus(it.otherCosts ?: BigDecimal.ZERO)
    }

    private fun ProjectPartnerBudgetOptions.toRow(flatRates: BudgetCostsCalculationResultFull) = ControlDeductionOverviewRow(
        typologyOfErrorId = null,
        typologyOfErrorName = null,
        staffCost = if (staffCostsFlatRate == null) BigDecimal.ZERO else flatRates.staff,
        officeAndAdministration = if (officeAndAdministrationOnDirectCostsFlatRate == null
            && officeAndAdministrationOnStaffCostsFlatRate == null) BigDecimal.ZERO else flatRates.office,
        travelAndAccommodation = if (travelAndAccommodationOnStaffCostsFlatRate == null) BigDecimal.ZERO else flatRates.travel,
        externalExpertise = BigDecimal.ZERO,
        equipment = BigDecimal.ZERO,
        infrastructureAndWorks = BigDecimal.ZERO,
        lumpSums = BigDecimal.ZERO,
        unitCosts = BigDecimal.ZERO,
        otherCosts = if (otherCostsOnStaffCostsFlatRate == null) BigDecimal.ZERO else flatRates.other
    )

    private fun Map<Long, Map<BudgetCostCategory, BigDecimal>>.extractFor(error: TypologyErrors) = ControlDeductionOverviewRow(
        typologyOfErrorId = error.id,
        typologyOfErrorName = error.description,
        staffCost = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.Staff),
        officeAndAdministration = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.Office),
        travelAndAccommodation = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.Travel),
        externalExpertise = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.External),
        equipment = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.Equipment),
        infrastructureAndWorks = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.Infrastructure),
        lumpSums = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.LumpSum),
        unitCosts = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.UnitCost),
        otherCosts = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.Other),
    )
}
