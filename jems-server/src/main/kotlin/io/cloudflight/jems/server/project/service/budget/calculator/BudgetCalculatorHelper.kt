package io.cloudflight.jems.server.project.service.budget.calculator

import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import java.math.BigDecimal
import java.math.RoundingMode

fun calculateBudget(options: ProjectPartnerBudgetOptions, byCategory: Map<BudgetCostCategory, BigDecimal>): BudgetCostsCalculationResultFull {
    val sums = byCategory.toMutableMap()

    getStaffCosts(
        staffFlatRate = options.staffCostsFlatRate,
        travelFlatRate = options.travelAndAccommodationOnStaffCostsFlatRate,
        sums = sums,
    ).also { sums[BudgetCostCategory.Staff] = it }

    getTravelCosts(
        travelFlatRate = options.travelAndAccommodationOnStaffCostsFlatRate,
        sums = sums,
    ).also { sums[BudgetCostCategory.Travel] = it }

    getOfficeCosts(
        officeStaffFlatRate = options.officeAndAdministrationOnStaffCostsFlatRate,
        officeDirectFlatRate = options.officeAndAdministrationOnDirectCostsFlatRate,
        sums = sums,
    ).also { sums[BudgetCostCategory.Office] = it }

    getOtherCosts(
        staffFlatRate = options.staffCostsFlatRate,
        otherCostsFlatRate = options.otherCostsOnStaffCostsFlatRate,
        sums = sums,
    ).also { sums[BudgetCostCategory.Other] = it }

    return BudgetCostsCalculationResultFull(
        staff = sums.giveMe(BudgetCostCategory.Staff),
        office = sums.giveMe(BudgetCostCategory.Office),
        travel = sums.giveMe(BudgetCostCategory.Travel),
        external = sums.giveMe(BudgetCostCategory.External),
        equipment = sums.giveMe(BudgetCostCategory.Equipment),
        infrastructure = sums.giveMe(BudgetCostCategory.Infrastructure),
        other = sums.giveMe(BudgetCostCategory.Other),
        lumpSum = sums.giveMe(BudgetCostCategory.LumpSum),
        unitCost = sums.giveMe(BudgetCostCategory.UnitCost),
        sum = sums.values.sumOf { it },
    )
}

private fun getStaffCosts(staffFlatRate: Int?, travelFlatRate: Int?, sums: Map<BudgetCostCategory, BigDecimal>): BigDecimal {
    if (staffFlatRate == null)
        return sums.giveMe(BudgetCostCategory.Staff)

    val travel = if (travelFlatRate == null) sums.giveMe(BudgetCostCategory.Travel) else BigDecimal.ZERO
    return travel
        .plus(sums.giveMe(BudgetCostCategory.External))
        .plus(sums.giveMe(BudgetCostCategory.Equipment))
        .plus(sums.giveMe(BudgetCostCategory.Infrastructure))
        .applyFlatRate(staffFlatRate)
}

private fun getTravelCosts(travelFlatRate: Int?, sums: Map<BudgetCostCategory, BigDecimal>): BigDecimal {
    if (travelFlatRate == null)
        return sums.giveMe(BudgetCostCategory.Travel)

    return sums.giveMe(BudgetCostCategory.Staff)
        .applyFlatRate(travelFlatRate)
}

private fun getOfficeCosts(officeStaffFlatRate: Int?, officeDirectFlatRate: Int?, sums: Map<BudgetCostCategory, BigDecimal>): BigDecimal {
    if (officeStaffFlatRate == null && officeDirectFlatRate == null)
        return BigDecimal.ZERO

    val flatRate = officeStaffFlatRate ?: officeDirectFlatRate!!

    val base = if (officeStaffFlatRate != null)
        sums.giveMe(BudgetCostCategory.Staff)
    else
        setOf(
            sums.giveMe(BudgetCostCategory.Staff),
            sums.giveMe(BudgetCostCategory.Travel),
            sums.giveMe(BudgetCostCategory.External),
            sums.giveMe(BudgetCostCategory.Equipment),
            sums.giveMe(BudgetCostCategory.Infrastructure),
        ).sumOf { it }

    return base.applyFlatRate(flatRate)
}

private fun getOtherCosts(staffFlatRate: Int?, otherCostsFlatRate: Int?, sums: Map<BudgetCostCategory, BigDecimal>) =
    if (staffFlatRate != null || otherCostsFlatRate == null)
        BigDecimal.ZERO
    else
        sums.giveMe(BudgetCostCategory.Staff).applyFlatRate(otherCostsFlatRate)

private fun Map<BudgetCostCategory, BigDecimal>.giveMe(cat: BudgetCostCategory): BigDecimal {
    return getOrDefault(cat, BigDecimal.ZERO)
}

private fun BigDecimal.applyFlatRate(flatRate: Int) = this.multiply(
    flatRate.toBigDecimal().divide(BigDecimal.valueOf(100))
).setScale(2, RoundingMode.DOWN)
