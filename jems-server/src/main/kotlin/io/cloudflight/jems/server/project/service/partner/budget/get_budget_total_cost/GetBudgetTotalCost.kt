package io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost

import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectPartner
import io.cloudflight.jems.server.project.service.common.BudgetCostsCalculatorService
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_options.GetBudgetOptionsInteractor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetBudgetTotalCost(
    private val budgetCostsPersistence: ProjectPartnerBudgetCostsPersistence,
    private val getBudgetOptions: GetBudgetOptionsInteractor,
    private val budgetCostsCalculator: BudgetCostsCalculatorService
) : GetBudgetTotalCostInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProjectPartner
    override fun getBudgetTotalCost(partnerId: Long): BigDecimal {

        val budgetOptions = getBudgetOptions.getBudgetOptions(partnerId)

        val unitCostTotal = budgetCostsPersistence.getBudgetUnitCostTotal(partnerId)
        val lumpSumsTotal = budgetCostsPersistence.getBudgetLumpSumsCostTotal(partnerId)
        val equipmentCostTotal = budgetCostsPersistence.getBudgetEquipmentCostTotal(partnerId)
        val externalCostTotal = budgetCostsPersistence.getBudgetExternalExpertiseAndServicesCostTotal(partnerId)
        val infrastructureCostTotal = budgetCostsPersistence.getBudgetInfrastructureAndWorksCostTotal(partnerId)

        val travelCostTotal =
            fetchTravelCostsOrZero(partnerId, budgetOptions?.travelAndAccommodationOnStaffCostsFlatRate)

        val staffCostTotal = fetchStaffCostsOrZero(partnerId, budgetOptions?.staffCostsFlatRate,)

        return budgetCostsCalculator.calculateCosts(
            budgetOptions,
            unitCostTotal,
            lumpSumsTotal,
            externalCostTotal,
            equipmentCostTotal,
            infrastructureCostTotal,
            travelCostTotal,
            staffCostTotal
        ).totalCosts
    }

    private fun fetchTravelCostsOrZero(partnerId: Long, travelAndAccommodationOnStaffCostsFlatRate: Int?) =
        if (travelAndAccommodationOnStaffCostsFlatRate == null)
            budgetCostsPersistence.getBudgetTravelAndAccommodationCostTotal(partnerId)
        else
            BigDecimal.ZERO

    private fun fetchStaffCostsOrZero(partnerId: Long, staffCostsFlatRate: Int?, ) =
        if (staffCostsFlatRate == null)
            budgetCostsPersistence.getBudgetStaffCostTotal(partnerId)
        else
            BigDecimal.ZERO
}
