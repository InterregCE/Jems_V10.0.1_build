package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlDeductionOverview

import io.cloudflight.jems.server.project.service.budget.calculator.calculateBudget
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlDeductionOverviewRow
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.getCategory
import java.math.BigDecimal
import java.math.RoundingMode

fun Collection<BigDecimal?>.sum() = sumOf { it ?: BigDecimal.ZERO }

fun Collection<ProjectPartnerReportExpenditureVerification>.calculateTypology(
    options: ProjectPartnerBudgetOptions
): BudgetCostsCalculationResultFull {
    val sums = groupBy { it.getCategory() }
        .mapValues { it.value.sumOf { v-> v.declaredAmountAfterSubmission ?: BigDecimal.ZERO }.minus(it.value.sumOf { it.certifiedAmount }) }
    return calculateBudget(options, sums, RoundingMode.UP)
}

private val emptySumUp = ControlDeductionOverviewRow(
    typologyOfErrorId = null,
    typologyOfErrorName = null,
    staffCost = BigDecimal.ZERO,
    officeAndAdministration = BigDecimal.ZERO,
    travelAndAccommodation = BigDecimal.ZERO,
    externalExpertise = BigDecimal.ZERO,
    equipment = BigDecimal.ZERO,
    infrastructureAndWorks = BigDecimal.ZERO,
    lumpSums = BigDecimal.ZERO,
    unitCosts = BigDecimal.ZERO,
    otherCosts = BigDecimal.ZERO,
    total = BigDecimal.ZERO,
)

fun List<ControlDeductionOverviewRow>?.sumUp(): ControlDeductionOverviewRow {
    if (this == null)
        return emptySumUp
    return this.fold(emptySumUp) { first, second ->
        ControlDeductionOverviewRow(
            typologyOfErrorId = null,
            typologyOfErrorName = null,
            staffCost = first.staffCost!!.plus(second.staffCost ?: BigDecimal.ZERO),
            officeAndAdministration = first.officeAndAdministration!!.plus(second.officeAndAdministration ?: BigDecimal.ZERO),
            travelAndAccommodation = first.travelAndAccommodation!!.plus(second.travelAndAccommodation ?: BigDecimal.ZERO),
            externalExpertise = first.externalExpertise!!.plus(second.externalExpertise ?: BigDecimal.ZERO),
            equipment = first.equipment!!.plus(second.equipment ?: BigDecimal.ZERO),
            infrastructureAndWorks = first.infrastructureAndWorks!!.plus(second.infrastructureAndWorks ?: BigDecimal.ZERO),
            lumpSums = first.lumpSums!!.plus(second.lumpSums ?: BigDecimal.ZERO),
            unitCosts = first.unitCosts!!.plus(second.unitCosts ?: BigDecimal.ZERO),
            otherCosts = first.otherCosts!!.plus(second.otherCosts ?: BigDecimal.ZERO),
            total = first.total.plus(second.total),
        )
    }
}
