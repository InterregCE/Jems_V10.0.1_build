package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCostCategoryBreakdown

import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.report.fillInOverviewFields
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.ReportCertificateCostCategory
import java.math.BigDecimal
import java.math.RoundingMode

fun ReportCertificateCostCategory.toLinesModel() = CertificateCostCategoryBreakdown(
    staff = CertificateCostCategoryBreakdownLine(
        totalEligibleBudget = totalsFromAF.staff,
        previouslyReported = previouslyReported.staff,
        currentReport = currentlyReported.staff,
    ),
    office = CertificateCostCategoryBreakdownLine(
        totalEligibleBudget = totalsFromAF.office,
        previouslyReported = previouslyReported.office,
        currentReport = currentlyReported.office,
    ),
    travel = CertificateCostCategoryBreakdownLine(
        totalEligibleBudget = totalsFromAF.travel,
        previouslyReported = previouslyReported.travel,
        currentReport = currentlyReported.travel,
    ),
    external = CertificateCostCategoryBreakdownLine(
        totalEligibleBudget = totalsFromAF.external,
        previouslyReported = previouslyReported.external,
        currentReport = currentlyReported.external,
    ),
    equipment = CertificateCostCategoryBreakdownLine(
        totalEligibleBudget = totalsFromAF.equipment,
        previouslyReported = previouslyReported.equipment,
        currentReport = currentlyReported.equipment,
    ),
    infrastructure = CertificateCostCategoryBreakdownLine(
        totalEligibleBudget = totalsFromAF.infrastructure,
        previouslyReported = previouslyReported.infrastructure,
        currentReport = currentlyReported.infrastructure,
    ),
    other = CertificateCostCategoryBreakdownLine(
        totalEligibleBudget = totalsFromAF.other,
        previouslyReported = previouslyReported.other,
        currentReport = currentlyReported.other,
    ),
    lumpSum = CertificateCostCategoryBreakdownLine(
        totalEligibleBudget = totalsFromAF.lumpSum,
        previouslyReported = previouslyReported.lumpSum,
        currentReport = currentlyReported.lumpSum,
    ),
    unitCost = CertificateCostCategoryBreakdownLine(
        totalEligibleBudget = totalsFromAF.unitCost,
        previouslyReported = previouslyReported.unitCost,
        currentReport = currentlyReported.unitCost,
    ),
    total = CertificateCostCategoryBreakdownLine(
        totalEligibleBudget = totalsFromAF.sum,
        previouslyReported = previouslyReported.sum,
        currentReport = currentlyReported.sum,
    ),
)

fun CertificateCostCategoryBreakdown.fillInCurrent(current: BudgetCostsCalculationResultFull) = apply {
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

fun CertificateCostCategoryBreakdown.fillInOverviewFields() = apply {
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

