package io.cloudflight.jems.server.project.service.common

import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResult
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import java.math.BigDecimal

interface BudgetCostsCalculatorService {

    fun calculateCosts(
        budgetOptions: ProjectPartnerBudgetOptions?,
        unitCosts: BigDecimal,
        lumpSumsCosts: BigDecimal,
        externalCosts: BigDecimal,
        equipmentCosts: BigDecimal,
        infrastructureCosts: BigDecimal,
        travelCosts: BigDecimal,
        staffCosts: BigDecimal,
        spfCosts: BigDecimal,
    ): BudgetCostsCalculationResult
}
