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
    override fun getBudgetTotalCost(partnerId: Long, version: Int?): BigDecimal {

        val budgetOptions = getBudgetOptions.getBudgetOptions(partnerId)

        val unitCostTotal = budgetCostsPersistence.getBudgetUnitCostTotal(partnerId, version)

        val lumpSumsTotal = budgetCostsPersistence.getBudgetLumpSumsCostTotal(partnerId, version)
        val equipmentCostTotal = budgetCostsPersistence.getBudgetEquipmentCostTotal(partnerId, version)
        val externalCostTotal =
            budgetCostsPersistence.getBudgetExternalExpertiseAndServicesCostTotal(partnerId, version)
        val infrastructureCostTotal =
            budgetCostsPersistence.getBudgetInfrastructureAndWorksCostTotal(partnerId, version)

        val travelCostTotal =
            fetchTravelCostsOrZero(partnerId, budgetOptions?.travelAndAccommodationOnStaffCostsFlatRate, version)

        val staffCostTotal = fetchStaffCostsOrZero(partnerId, budgetOptions?.staffCostsFlatRate, version)

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

    private fun fetchTravelCostsOrZero(
        partnerId: Long, travelAndAccommodationOnStaffCostsFlatRate: Int?, version: Int?
    ) =
        if (travelAndAccommodationOnStaffCostsFlatRate == null)
            budgetCostsPersistence.getBudgetTravelAndAccommodationCostTotal(partnerId, version)
        else
            BigDecimal.ZERO

    private fun fetchStaffCostsOrZero(partnerId: Long, staffCostsFlatRate: Int?, version: Int?) =
        if (staffCostsFlatRate == null)
            budgetCostsPersistence.getBudgetStaffCostTotal(partnerId, version)
        else
            BigDecimal.ZERO
}
