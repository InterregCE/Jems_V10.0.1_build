package io.cloudflight.jems.server.project.service.common

import io.cloudflight.jems.server.project.service.budget.calculator.BudgetCostCategory
import io.cloudflight.jems.server.project.service.budget.calculator.calculateBudget
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResult
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class BudgetCostsCalculator : BudgetCostsCalculatorService {

    override fun calculateCosts(
        budgetOptions: ProjectPartnerBudgetOptions?,
        unitCosts: BigDecimal,
        lumpSumsCosts: BigDecimal,
        externalCosts: BigDecimal,
        equipmentCosts: BigDecimal,
        infrastructureCosts: BigDecimal,
        travelCosts: BigDecimal,
        staffCosts: BigDecimal
    ): BudgetCostsCalculationResult {

        val byCategory: Map<BudgetCostCategory, BigDecimal> = mapOf(
            BudgetCostCategory.Staff to staffCosts,
            BudgetCostCategory.Travel to travelCosts,
            BudgetCostCategory.External to externalCosts,
            BudgetCostCategory.Equipment to equipmentCosts,
            BudgetCostCategory.Infrastructure to infrastructureCosts,
            BudgetCostCategory.LumpSum to lumpSumsCosts,
            BudgetCostCategory.UnitCost to unitCosts,
        )

        return calculateBudget(
            options = budgetOptions ?: ProjectPartnerBudgetOptions(partnerId = 0L),
            byCategory = byCategory,
        ).let { BudgetCostsCalculationResult(
            staffCosts = it.staff,
            travelCosts = it.travel,
            officeAndAdministrationCosts = it.office,
            otherCosts = it.other,
            totalCosts = it.sum,
        ) }
    }

}
