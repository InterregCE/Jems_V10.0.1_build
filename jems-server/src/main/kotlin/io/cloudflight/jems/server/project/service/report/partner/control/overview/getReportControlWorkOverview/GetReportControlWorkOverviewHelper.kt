package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview

import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCurrentValuesWrapper
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureCostAfterControl
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.calculateCostCategoriesFor
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.calculateCurrent
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown.isNonZero
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown.isZero
import java.math.BigDecimal

fun Collection<ExpenditureCostAfterControl>.calculateCertified(
    options: ProjectPartnerBudgetOptions
): BudgetCostsCalculationResultFull =
    calculateCostCategoriesFor(options) { it.certifiedAmount }

fun Collection<ProjectPartnerReportExpenditureVerification>.onlyParkedOnes() =
    filter { it.parked }

fun getParkedAndEligibleAfterControl(
    currentExpenditures: Collection<ProjectPartnerReportExpenditureVerification>,
    costCategories: ReportExpenditureCostCategory,
): BudgetCostsCurrentValuesWrapper {
    val eligibleAfterControl = currentExpenditures.calculateCertified(costCategories.options)
    var parked = currentExpenditures.onlyParkedOnes().calculateCurrent(costCategories.options)
    val thereAreNoDeductions = currentExpenditures.all { it.parked || it.deductedAmount.isZero() }

    val deducted = costCategories.currentlyReported.sum.minus(eligibleAfterControl.sum).minus(parked.sum)
    val performElimination = thereAreNoDeductions && deducted.isNonZero()
    if (performElimination) {
        parked = parked.add(amount = deducted, addTo = currentExpenditures.onlyParkedOnes().first().costCategory)
    }

    return BudgetCostsCurrentValuesWrapper(
        currentlyReported = eligibleAfterControl,
        currentlyReportedParked = parked,
    )
}

data class ParkedAndEligibleAfterControl(
    val eligibleAfterControl: BigDecimal,
    val parked: BigDecimal,
)

private fun BudgetCostsCalculationResultFull.add(amount: BigDecimal, addTo: ReportBudgetCategory): BudgetCostsCalculationResultFull {
    return when (addTo) {
        ReportBudgetCategory.StaffCosts -> this.copy(staff = this.staff.plus(amount), sum = this.sum.plus(amount))
        ReportBudgetCategory.OfficeAndAdministrationCosts -> this.copy(office = this.office.plus(amount), sum = this.sum.plus(amount))
        ReportBudgetCategory.TravelAndAccommodationCosts -> this.copy(travel = this.travel.plus(amount), sum = this.sum.plus(amount))
        ReportBudgetCategory.ExternalCosts -> this.copy(external = this.external.plus(amount), sum = this.sum.plus(amount))
        ReportBudgetCategory.EquipmentCosts -> this.copy(equipment = this.equipment.plus(amount), sum = this.sum.plus(amount))
        ReportBudgetCategory.InfrastructureCosts -> this.copy(infrastructure = this.infrastructure.plus(amount), sum = this.sum.plus(amount))
        ReportBudgetCategory.Multiple -> this.copy(other = this.other.plus(amount), sum = this.sum.plus(amount))
        ReportBudgetCategory.SpfCosts -> this.copy(spfCost = this.spfCost.plus(amount), sum = this.sum.plus(amount))
    }
}

fun BudgetCostsCalculationResultFull.extractFlatRatesSum(options: ProjectPartnerBudgetOptions): BigDecimal =
    with(options) {
        listOf(
            if (hasFlatRateOffice()) office else BigDecimal.ZERO,
            if (hasFlatRateTravel()) travel else BigDecimal.ZERO,
            if (hasFlatRateStaff()) staff else BigDecimal.ZERO,
            if (hasFlatRateOther()) other else BigDecimal.ZERO,
        ).sumOf { it }
    }
