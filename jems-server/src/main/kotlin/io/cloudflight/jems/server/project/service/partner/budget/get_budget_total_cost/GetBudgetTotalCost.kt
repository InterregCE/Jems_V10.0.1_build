package io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost

import io.cloudflight.jems.server.project.authorization.CanReadProjectPartner
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetPersistence
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_options.GetBudgetOptionsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.percentage
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetBudgetTotalCost(private val persistence: ProjectPartnerBudgetPersistence, private val getBudgetOptions: GetBudgetOptionsInteractor) : GetBudgetTotalCostInteractor {

    @Transactional(readOnly = true)
    @CanReadProjectPartner
    override fun getBudgetTotalCost(partnerId: Long): BigDecimal {

        val budgetOptions = getBudgetOptions.getBudgetOptions(partnerId)

        val equipmentCostTotal = persistence.getBudgetEquipmentCostTotal(partnerId)
        val externalExpertiseAndServicesCostTotal = persistence.getBudgetExternalExpertiseAndServicesCostTotal(partnerId)
        val infrastructureAndWorksCostTotal = persistence.getBudgetInfrastructureAndWorksCostTotal(partnerId)

        var travelAndAccommodationCostTotal =
            if (budgetOptions?.travelAndAccommodationOnStaffCostsFlatRate == null)
                persistence.getBudgetTravelAndAccommodationCostTotal(partnerId)
            else
                BigDecimal.ZERO

        val staffCostTotal =
            if (budgetOptions?.staffCostsFlatRate == null)
                persistence.getBudgetStaffCostTotal(partnerId)
            else
                calculateStaffCosts(externalExpertiseAndServicesCostTotal, equipmentCostTotal, infrastructureAndWorksCostTotal, travelAndAccommodationCostTotal, budgetOptions.staffCostsFlatRate, budgetOptions.travelAndAccommodationOnStaffCostsFlatRate)

        if (budgetOptions?.travelAndAccommodationOnStaffCostsFlatRate != null)
            travelAndAccommodationCostTotal = calculateTravelCosts(staffCostTotal, budgetOptions.travelAndAccommodationOnStaffCostsFlatRate)

        return staffCostTotal
            .add(travelAndAccommodationCostTotal)
            .add(externalExpertiseAndServicesCostTotal)
            .add(equipmentCostTotal)
            .add(infrastructureAndWorksCostTotal)
            .add(calculateOfficeAndAdministrationCosts(staffCostTotal, budgetOptions?.officeAndAdministrationOnStaffCostsFlatRate))
            .add(calculateOtherCosts(staffCostTotal, budgetOptions?.otherCostsOnStaffCostsFlatRate, budgetOptions?.staffCostsFlatRate))
    }

}

private fun calculateStaffCosts(externalCosts: BigDecimal, equipmentCosts: BigDecimal, infrastructureCosts: BigDecimal, travelCosts: BigDecimal, staffCostsFlatRate: Int, travelAndAccommodationOnStaffCostsFlatRate: Int?) =
    if (travelAndAccommodationOnStaffCostsFlatRate != null)
        externalCosts.add(equipmentCosts).add(infrastructureCosts).percentage(staffCostsFlatRate)
    else
        travelCosts.add(externalCosts).add(equipmentCosts).add(infrastructureCosts).percentage(staffCostsFlatRate)

private fun calculateTravelCosts(staffCostTotal: BigDecimal, travelAndAccommodationOnStaffCostsFlatRate: Int) =
    staffCostTotal.percentage(travelAndAccommodationOnStaffCostsFlatRate)


private fun calculateOfficeAndAdministrationCosts(staffCostTotal: BigDecimal, officeAndAdministrationOnStaffCostsFlatRate: Int?): BigDecimal =
    if (officeAndAdministrationOnStaffCostsFlatRate != null)
        staffCostTotal.percentage(officeAndAdministrationOnStaffCostsFlatRate)
    else
        BigDecimal.ZERO

private fun calculateOtherCosts(staffCostTotal: BigDecimal, otherCostsOnStaffCostsFlatRate: Int?, staffCostsFlatRate: Int?): BigDecimal =
    if (otherCostsOnStaffCostsFlatRate != null) {
        if (staffCostsFlatRate == null)
            staffCostTotal.percentage(otherCostsOnStaffCostsFlatRate)
        else
            BigDecimal.ZERO
    } else
        BigDecimal.ZERO
