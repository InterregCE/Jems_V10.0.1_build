package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown

import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import io.cloudflight.jems.server.project.service.budget.calculator.calculateBudget
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.fillInOverviewFields
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureCostAfterSubmission
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureCostWithCategory
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ExpenditureCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ExpenditureCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.partner.expenditure.fillCurrencyRates
import java.math.BigDecimal

fun ReportExpenditureCostCategory.toLinesModel() = ExpenditureCostCategoryBreakdown(
    staff = ExpenditureCostCategoryBreakdownLine(
        flatRate = options.staffCostsFlatRate,
        totalEligibleBudget = totalsFromAF.staff,
        previouslyReported = previouslyReported.staff,
        currentReport = currentlyReported.staff,
        totalEligibleAfterControl = totalEligibleAfterControl.staff,
        currentReportReIncluded = currentlyReportedReIncluded.staff,
        previouslyReportedParked = previouslyReportedParked.staff,
        previouslyValidated = previouslyValidated.staff,
    ),
    office = ExpenditureCostCategoryBreakdownLine(
        flatRate = options.officeAndAdministrationOnStaffCostsFlatRate ?: options.officeAndAdministrationOnDirectCostsFlatRate,
        totalEligibleBudget = totalsFromAF.office,
        previouslyReported = previouslyReported.office,
        currentReport = currentlyReported.office,
        totalEligibleAfterControl = totalEligibleAfterControl.office,
        currentReportReIncluded = currentlyReportedReIncluded.office,
        previouslyReportedParked = previouslyReportedParked.office,
        previouslyValidated = previouslyValidated.office,
    ),
    travel = ExpenditureCostCategoryBreakdownLine(
        flatRate = options.travelAndAccommodationOnStaffCostsFlatRate,
        totalEligibleBudget = totalsFromAF.travel,
        previouslyReported = previouslyReported.travel,
        currentReport = currentlyReported.travel,
        totalEligibleAfterControl = totalEligibleAfterControl.travel,
        currentReportReIncluded = currentlyReportedReIncluded.travel,
        previouslyReportedParked = previouslyReportedParked.travel,
        previouslyValidated = previouslyValidated.travel,
    ),
    external = ExpenditureCostCategoryBreakdownLine(
        flatRate = null,
        totalEligibleBudget = totalsFromAF.external,
        previouslyReported = previouslyReported.external,
        currentReport = currentlyReported.external,
        totalEligibleAfterControl = totalEligibleAfterControl.external,
        currentReportReIncluded = currentlyReportedReIncluded.external,
        previouslyReportedParked = previouslyReportedParked.external,
        previouslyValidated = previouslyValidated.external,
    ),
    equipment = ExpenditureCostCategoryBreakdownLine(
        flatRate = null,
        totalEligibleBudget = totalsFromAF.equipment,
        previouslyReported = previouslyReported.equipment,
        currentReport = currentlyReported.equipment,
        totalEligibleAfterControl = totalEligibleAfterControl.equipment,
        currentReportReIncluded = currentlyReportedReIncluded.equipment,
        previouslyReportedParked = previouslyReportedParked.equipment,
        previouslyValidated = previouslyValidated.equipment,
    ),
    infrastructure = ExpenditureCostCategoryBreakdownLine(
        flatRate = null,
        totalEligibleBudget = totalsFromAF.infrastructure,
        previouslyReported = previouslyReported.infrastructure,
        currentReport = currentlyReported.infrastructure,
        totalEligibleAfterControl = totalEligibleAfterControl.infrastructure,
        currentReportReIncluded = currentlyReportedReIncluded.infrastructure,
        previouslyReportedParked = previouslyReportedParked.infrastructure,
        previouslyValidated = previouslyValidated.infrastructure,
    ),
    other = ExpenditureCostCategoryBreakdownLine(
        flatRate = options.otherCostsOnStaffCostsFlatRate,
        totalEligibleBudget = totalsFromAF.other,
        previouslyReported = previouslyReported.other,
        currentReport = currentlyReported.other,
        totalEligibleAfterControl = totalEligibleAfterControl.other,
        currentReportReIncluded = currentlyReportedReIncluded.other,
        previouslyReportedParked = previouslyReportedParked.other,
        previouslyValidated = previouslyValidated.other,
    ),
    lumpSum = ExpenditureCostCategoryBreakdownLine(
        flatRate = null,
        totalEligibleBudget = totalsFromAF.lumpSum,
        previouslyReported = previouslyReported.lumpSum,
        currentReport = currentlyReported.lumpSum,
        totalEligibleAfterControl = totalEligibleAfterControl.lumpSum,
        currentReportReIncluded = currentlyReportedReIncluded.lumpSum,
        previouslyReportedParked = previouslyReportedParked.lumpSum,
        previouslyValidated = previouslyValidated.lumpSum,
    ),
    unitCost = ExpenditureCostCategoryBreakdownLine(
        flatRate = null,
        totalEligibleBudget = totalsFromAF.unitCost,
        previouslyReported = previouslyReported.unitCost,
        currentReport = currentlyReported.unitCost,
        totalEligibleAfterControl = totalEligibleAfterControl.unitCost,
        currentReportReIncluded = currentlyReportedReIncluded.unitCost,
        previouslyReportedParked = previouslyReportedParked.unitCost,
        previouslyValidated = previouslyValidated.unitCost,
    ),
    spfCost = ExpenditureCostCategoryBreakdownLine(
        flatRate = null,
        totalEligibleBudget = totalsFromAF.spfCost,
        previouslyReported = previouslyReported.spfCost,
        currentReport = currentlyReported.spfCost,
        totalEligibleAfterControl = totalEligibleAfterControl.spfCost,
        currentReportReIncluded = currentlyReportedReIncluded.spfCost,
        previouslyReportedParked = previouslyReportedParked.spfCost,
        previouslyValidated = previouslyValidated.spfCost,
    ),
    total = ExpenditureCostCategoryBreakdownLine(
        flatRate = null,
        totalEligibleBudget = totalsFromAF.sum,
        previouslyReported = previouslyReported.sum,
        currentReport = currentlyReported.sum,
        totalEligibleAfterControl = totalEligibleAfterControl.sum,
        currentReportReIncluded = currentlyReportedReIncluded.sum,
        previouslyReportedParked = previouslyReportedParked.sum,
        previouslyValidated = previouslyValidated.sum,
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
    spfCost.currentReport = current.spfCost
    total.currentReport = current.sum
}

fun ExpenditureCostCategoryBreakdown.fillInCurrentReIncluded(currentReIncluded: BudgetCostsCalculationResultFull) = apply {
    staff.currentReportReIncluded = currentReIncluded.staff
    office.currentReportReIncluded = currentReIncluded.office
    travel.currentReportReIncluded = currentReIncluded.travel
    external.currentReportReIncluded = currentReIncluded.external
    equipment.currentReportReIncluded = currentReIncluded.equipment
    infrastructure.currentReportReIncluded = currentReIncluded.infrastructure
    other.currentReportReIncluded = currentReIncluded.other
    lumpSum.currentReportReIncluded = currentReIncluded.lumpSum
    unitCost.currentReportReIncluded = currentReIncluded.unitCost
    spfCost.currentReportReIncluded = currentReIncluded.spfCost
    total.currentReportReIncluded = currentReIncluded.sum
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
    spfCost.fillInOverviewFields()
    total.fillInOverviewFields()
}

fun Collection<ExpenditureCostAfterSubmission>.calculateCurrent(options: ProjectPartnerBudgetOptions): BudgetCostsCalculationResultFull =
    calculateCostCategoriesFor(options) { it.declaredAmountAfterSubmission }

fun <T: ExpenditureCostWithCategory> Collection<T>.calculateCostCategoriesFor(
    options: ProjectPartnerBudgetOptions,
    valueResolver: (T) -> BigDecimal?,
): BudgetCostsCalculationResultFull {
    val sums = groupBy { cost -> cost.getCategory() }
        .mapValues { (_, expendituresOfCategory) ->
            expendituresOfCategory.sumOf { valueResolver.invoke(it) ?: BigDecimal.ZERO }
        }

    return calculateBudget(options, sums)
}

fun List<ProjectPartnerReportExpenditureCost>.fillActualCurrencyRates(rates: Collection<CurrencyConversion>) = apply {
    val ratesByCode = rates.associateBy { it.code }
    this.fillCurrencyRates(ratesByCode)
}

fun Collection<ExpenditureCost>.onlyReIncluded() =
    filter { it.parkingMetadata != null }
