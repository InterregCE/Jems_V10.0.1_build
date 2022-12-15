package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown

import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import io.cloudflight.jems.server.project.service.budget.calculator.BudgetCostCategory
import io.cloudflight.jems.server.project.service.budget.calculator.BudgetCostCategory.Staff
import io.cloudflight.jems.server.project.service.budget.calculator.BudgetCostCategory.Office
import io.cloudflight.jems.server.project.service.budget.calculator.BudgetCostCategory.Travel
import io.cloudflight.jems.server.project.service.budget.calculator.BudgetCostCategory.External
import io.cloudflight.jems.server.project.service.budget.calculator.BudgetCostCategory.Equipment
import io.cloudflight.jems.server.project.service.budget.calculator.BudgetCostCategory.Infrastructure
import io.cloudflight.jems.server.project.service.budget.calculator.BudgetCostCategory.LumpSum
import io.cloudflight.jems.server.project.service.budget.calculator.BudgetCostCategory.UnitCost
import io.cloudflight.jems.server.project.service.budget.calculator.calculateBudget
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ExpenditureCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ExpenditureCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.partner.expenditure.fillCurrencyRates
import java.math.BigDecimal
import java.math.RoundingMode

fun ReportExpenditureCostCategory.toLinesModel() = ExpenditureCostCategoryBreakdown(
    staff = ExpenditureCostCategoryBreakdownLine(
        flatRate = options.staffCostsFlatRate,
        totalEligibleBudget = totalsFromAF.staff,
        previouslyReported = previouslyReported.staff,
        currentReport = currentlyReported.staff,
        totalEligibleAfterControl = totalEligibleAfterControl.staff,
    ),
    office = ExpenditureCostCategoryBreakdownLine(
        flatRate = options.officeAndAdministrationOnStaffCostsFlatRate ?: options.officeAndAdministrationOnDirectCostsFlatRate,
        totalEligibleBudget = totalsFromAF.office,
        previouslyReported = previouslyReported.office,
        currentReport = currentlyReported.office,
        totalEligibleAfterControl = totalEligibleAfterControl.office,
    ),
    travel = ExpenditureCostCategoryBreakdownLine(
        flatRate = options.travelAndAccommodationOnStaffCostsFlatRate,
        totalEligibleBudget = totalsFromAF.travel,
        previouslyReported = previouslyReported.travel,
        currentReport = currentlyReported.travel,
        totalEligibleAfterControl = totalEligibleAfterControl.travel,
    ),
    external = ExpenditureCostCategoryBreakdownLine(
        flatRate = null,
        totalEligibleBudget = totalsFromAF.external,
        previouslyReported = previouslyReported.external,
        currentReport = currentlyReported.external,
        totalEligibleAfterControl = totalEligibleAfterControl.external,
    ),
    equipment = ExpenditureCostCategoryBreakdownLine(
        flatRate = null,
        totalEligibleBudget = totalsFromAF.equipment,
        previouslyReported = previouslyReported.equipment,
        currentReport = currentlyReported.equipment,
        totalEligibleAfterControl = totalEligibleAfterControl.equipment,
    ),
    infrastructure = ExpenditureCostCategoryBreakdownLine(
        flatRate = null,
        totalEligibleBudget = totalsFromAF.infrastructure,
        previouslyReported = previouslyReported.infrastructure,
        currentReport = currentlyReported.infrastructure,
        totalEligibleAfterControl = totalEligibleAfterControl.infrastructure,
    ),
    other = ExpenditureCostCategoryBreakdownLine(
        flatRate = options.otherCostsOnStaffCostsFlatRate,
        totalEligibleBudget = totalsFromAF.other,
        previouslyReported = previouslyReported.other,
        currentReport = currentlyReported.other,
        totalEligibleAfterControl = totalEligibleAfterControl.other,
    ),
    lumpSum = ExpenditureCostCategoryBreakdownLine(
        flatRate = null,
        totalEligibleBudget = totalsFromAF.lumpSum,
        previouslyReported = previouslyReported.lumpSum,
        currentReport = currentlyReported.lumpSum,
        totalEligibleAfterControl = totalEligibleAfterControl.lumpSum,
    ),
    unitCost = ExpenditureCostCategoryBreakdownLine(
        flatRate = null,
        totalEligibleBudget = totalsFromAF.unitCost,
        previouslyReported = previouslyReported.unitCost,
        currentReport = currentlyReported.unitCost,
        totalEligibleAfterControl = totalEligibleAfterControl.unitCost,
    ),
    total = ExpenditureCostCategoryBreakdownLine(
        flatRate = null,
        totalEligibleBudget = totalsFromAF.sum,
        previouslyReported = previouslyReported.sum,
        currentReport = currentlyReported.sum,
        totalEligibleAfterControl = totalEligibleAfterControl.sum,
    ),
)

fun ExpenditureCostCategoryBreakdown.fillInCurrent(current: BudgetCostsCalculationResultFull) = apply {
    staff.currentReport = current.staff
    office.currentReport = current.office
    travel.currentReport = current.travel
    external.currentReport = current.external
    equipment.currentReport = current.equipment
    infrastructure.currentReport = current.infrastructure
    other.currentReport = current.other
    lumpSum.currentReport = current.lumpSum
    unitCost.currentReport = current.unitCost
    total.currentReport = current.sum
}

fun ExpenditureCostCategoryBreakdown.fillInOverviewFields() = apply {
    staff.fillInOverviewFields()
    office.fillInOverviewFields()
    travel.fillInOverviewFields()
    external.fillInOverviewFields()
    equipment.fillInOverviewFields()
    infrastructure.fillInOverviewFields()
    other.fillInOverviewFields()
    lumpSum.fillInOverviewFields()
    unitCost.fillInOverviewFields()
    total.fillInOverviewFields()
}

private fun ExpenditureCostCategoryBreakdownLine.fillInOverviewFields() = apply {
    totalReportedSoFar = previouslyReported.plus(currentReport)
    totalReportedSoFarPercentage = totalReportedSoFar.percentageOf(totalEligibleBudget)
    remainingBudget = totalEligibleBudget.minus(totalReportedSoFar)
}

fun BigDecimal.percentageOf(total: BigDecimal): BigDecimal =
    if (total.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO
    else this.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP)

private fun ReportBudgetCategory.translateCostCategory(): BudgetCostCategory {
    return when (this) {
        ReportBudgetCategory.StaffCosts -> Staff
        ReportBudgetCategory.OfficeAndAdministrationCosts -> Office
        ReportBudgetCategory.TravelAndAccommodationCosts -> Travel
        ReportBudgetCategory.ExternalCosts -> External
        ReportBudgetCategory.EquipmentCosts -> Equipment
        ReportBudgetCategory.InfrastructureCosts -> Infrastructure
        ReportBudgetCategory.Multiple -> UnitCost
    }
}

fun ExpenditureCost.getCategory(): BudgetCostCategory =
    when {
        lumpSumId != null -> LumpSum
        else -> costCategory.translateCostCategory()
    }

fun Collection<ExpenditureCost>.calculateCurrent(options: ProjectPartnerBudgetOptions): BudgetCostsCalculationResultFull {
    val sums = groupBy { it.getCategory() }
        .mapValues { it.value.sumOf { it.declaredAmountAfterSubmission ?: BigDecimal.ZERO } }
    return calculateBudget(options, sums)
}

fun List<ProjectPartnerReportExpenditureCost>.fillActualCurrencyRates(rates: Collection<CurrencyConversion>) = apply {
    val ratesByCode = rates.associateBy { it.code }
    this.fillCurrencyRates(ratesByCode)
}
