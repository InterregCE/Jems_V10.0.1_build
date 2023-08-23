package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview

import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.calculateCostCategoriesFor
import java.math.BigDecimal

fun Collection<ProjectPartnerReportExpenditureVerification>.calculateCertified(
    options: ProjectPartnerBudgetOptions
): BudgetCostsCalculationResultFull =
    calculateCostCategoriesFor(options) { it.certifiedAmount }

fun Collection<ProjectPartnerReportExpenditureVerification>.onlyParkedOnes() =
    filter { it.parked }

fun BudgetCostsCalculationResultFull.extractFlatRatesSum(options: ProjectPartnerBudgetOptions): BigDecimal =
    with(options) {
        listOf(
            if (hasFlatRateOffice()) office else BigDecimal.ZERO,
            if (hasFlatRateTravel()) travel else BigDecimal.ZERO,
            if (hasFlatRateStaff()) staff else BigDecimal.ZERO,
            if (hasFlatRateOther()) other else BigDecimal.ZERO,
        ).sumOf { it }
    }
