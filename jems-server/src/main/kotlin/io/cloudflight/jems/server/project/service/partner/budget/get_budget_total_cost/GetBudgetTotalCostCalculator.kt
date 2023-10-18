package io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost

import io.cloudflight.jems.server.project.service.common.BudgetCostsCalculatorService
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetBudgetTotalCostCalculator(
    private val budgetCostsPersistence: ProjectPartnerBudgetCostsPersistence,
    private val budgetOptionsPersistence: ProjectPartnerBudgetOptionsPersistence,
    private val budgetCostsCalculator: BudgetCostsCalculatorService
) {

    @Transactional(readOnly = true)
    fun getBudgetTotalCost(partnerId: Long, version: String?): BigDecimal {
        val budgetUnitCostTotal = budgetCostsPersistence.getBudgetUnitCostTotal(partnerId, version)
        val budgetOptions = budgetOptionsPersistence.getBudgetOptions(partnerId, version)

        val lumpSumsTotal = budgetCostsPersistence.getBudgetLumpSumsCostTotal(partnerId, version)
        val equipmentCostTotal = budgetCostsPersistence.getBudgetEquipmentCostTotal(partnerId, version)
        val externalCostTotal =
            budgetCostsPersistence.getBudgetExternalExpertiseAndServicesCostTotal(partnerId, version)
        val infrastructureCostTotal =
            budgetCostsPersistence.getBudgetInfrastructureAndWorksCostTotal(partnerId, version)

        val travelCostTotal =
            fetchTravelCostsOrZero(partnerId, budgetOptions?.travelAndAccommodationOnStaffCostsFlatRate, version)

        val staffCostTotal = fetchStaffCostsOrZero(partnerId, budgetOptions?.staffCostsFlatRate, version)
        val spfCosts = budgetCostsPersistence.getBudgetSpfCostTotal(partnerId, version)

        return budgetCostsCalculator.calculateCosts(
            budgetOptions,
            budgetUnitCostTotal,
            lumpSumsTotal,
            externalCostTotal,
            equipmentCostTotal,
            infrastructureCostTotal,
            travelCostTotal,
            staffCostTotal,
            spfCosts = spfCosts,
        ).totalCosts
    }

    @Transactional(readOnly = true)
    fun getBudgetTotalSpfCost(partnerId: Long, version: String?): BigDecimal {
        return budgetCostsPersistence.getBudgetSpfCostTotal(partnerId, version)
    }

    private fun fetchTravelCostsOrZero(
        partnerId: Long, travelAndAccommodationOnStaffCostsFlatRate: Int?, version: String?
    ) =
        if (travelAndAccommodationOnStaffCostsFlatRate == null)
            budgetCostsPersistence.getBudgetTravelAndAccommodationCostTotal(partnerId, version)
        else
            BigDecimal.ZERO

    private fun fetchStaffCostsOrZero(partnerId: Long, staffCostsFlatRate: Int?, version: String?) =
        if (staffCostsFlatRate == null)
            budgetCostsPersistence.getBudgetStaffCostTotal(partnerId, version)
        else
            BigDecimal.ZERO
}
