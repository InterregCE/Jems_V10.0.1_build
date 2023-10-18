package io.cloudflight.jems.server.project.service.report.project.financialOverview.perPartner

import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner.PerPartnerCostCategoryBreakdownLine
import java.math.BigDecimal

private fun emptyLine() = BudgetCostsCalculationResultFull(
    staff = BigDecimal.ZERO,
    office = BigDecimal.ZERO,
    travel = BigDecimal.ZERO,
    external = BigDecimal.ZERO,
    equipment = BigDecimal.ZERO,
    infrastructure = BigDecimal.ZERO,
    other = BigDecimal.ZERO,
    lumpSum = BigDecimal.ZERO,
    unitCost = BigDecimal.ZERO,
    spfCost = BigDecimal.ZERO,
    sum = BigDecimal.ZERO,
)

fun Iterable<PerPartnerCostCategoryBreakdownLine>.sumOf(selector: (PerPartnerCostCategoryBreakdownLine) -> BudgetCostsCalculationResultFull) =
    fold(emptyLine()) { resultingTotalLine, line ->
        val row = selector.invoke(line)
        return@fold BudgetCostsCalculationResultFull(
            staff = resultingTotalLine.staff.plus(row.staff),
            office = resultingTotalLine.office.plus(row.office),
            travel = resultingTotalLine.travel.plus(row.travel),
            external = resultingTotalLine.external.plus(row.external),
            equipment = resultingTotalLine.equipment.plus(row.equipment),
            infrastructure = resultingTotalLine.infrastructure.plus(row.infrastructure),
            other = resultingTotalLine.other.plus(row.other),
            lumpSum = resultingTotalLine.lumpSum.plus(row.lumpSum),
            unitCost = resultingTotalLine.unitCost.plus(row.unitCost),
            spfCost = resultingTotalLine.spfCost.plus(row.spfCost),
            sum = resultingTotalLine.sum.plus(row.sum),
        )
    }
