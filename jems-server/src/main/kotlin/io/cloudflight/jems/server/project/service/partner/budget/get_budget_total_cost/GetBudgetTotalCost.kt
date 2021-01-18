package io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost

import io.cloudflight.jems.server.project.authorization.CanReadProjectPartner
import io.cloudflight.jems.server.project.service.common.BudgetCostsCalculatorService
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetPersistence
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_options.GetBudgetOptionsInteractor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetBudgetTotalCost(
    private val persistence: ProjectPartnerBudgetPersistence,
    private val getBudgetOptions: GetBudgetOptionsInteractor,
    private val budgetCostsCalculator: BudgetCostsCalculatorService
) : GetBudgetTotalCostInteractor {

    @Transactional(readOnly = true)
    @CanReadProjectPartner
    override fun getBudgetTotalCost(partnerId: Long): BigDecimal {

        val budgetOptions = getBudgetOptions.getBudgetOptions(partnerId)

        val unitCostTotal=persistence.getBudgetUnitCostTotal(partnerId)
        val lumpSumsTotal=persistence.getBudgetLumpSumsCostTotal(partnerId)
        val equipmentCostTotal = persistence.getBudgetEquipmentCostTotal(partnerId)
        val externalCostTotal = persistence.getBudgetExternalExpertiseAndServicesCostTotal(partnerId)
        val infrastructureCostTotal = persistence.getBudgetInfrastructureAndWorksCostTotal(partnerId)

        val travelCostTotal =
            fetchTravelCostsOrZero(partnerId, budgetOptions?.travelAndAccommodationOnStaffCostsFlatRate)

        val staffCostTotal = fetchStaffCostsOrZero(partnerId, budgetOptions?.staffCostsFlatRate,)

        val calculatedCosts = budgetCostsCalculator.calculateCosts(
            budgetOptions,
            externalCostTotal,
            equipmentCostTotal,
            infrastructureCostTotal,
            travelCostTotal,
            staffCostTotal
        )

        return calculatedCosts.staffCosts
            .add(calculatedCosts.travelCosts)
            .add(calculatedCosts.officeAndAdministrationCosts)
            .add(calculatedCosts.otherCosts)
            .add(externalCostTotal)
            .add(equipmentCostTotal)
            .add(infrastructureCostTotal)
            .add(unitCostTotal)
            .add(lumpSumsTotal)
    }

    private fun fetchTravelCostsOrZero(partnerId: Long, travelAndAccommodationOnStaffCostsFlatRate: Int?) =
        if (travelAndAccommodationOnStaffCostsFlatRate == null)
            persistence.getBudgetTravelAndAccommodationCostTotal(partnerId)
        else
            BigDecimal.ZERO

    private fun fetchStaffCostsOrZero(partnerId: Long, staffCostsFlatRate: Int?, ) =
        if (staffCostsFlatRate == null)
            persistence.getBudgetStaffCostTotal(partnerId)
        else
            BigDecimal.ZERO
}
