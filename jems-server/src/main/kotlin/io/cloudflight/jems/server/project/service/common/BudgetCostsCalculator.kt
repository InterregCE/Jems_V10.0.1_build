package io.cloudflight.jems.server.project.service.common

import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResult
import io.cloudflight.jems.server.project.service.partner.budget.percentage
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

        val finalStaffCosts =
            if (budgetOptions?.staffCostsFlatRate == null)
                staffCosts
            else
                calculateStaffCosts(
                    budgetOptions.staffCostsFlatRate,
                    budgetOptions.travelAndAccommodationOnStaffCostsFlatRate,
                    externalCosts,
                    equipmentCosts,
                    infrastructureCosts,
                    travelCosts
                )

        val finalTravelCosts =
            if (budgetOptions?.travelAndAccommodationOnStaffCostsFlatRate != null)
                calculateTravelCosts(
                    budgetOptions.travelAndAccommodationOnStaffCostsFlatRate,
                    staffCosts
                )
            else
                travelCosts

        val finalOfficeAndAdministrationCosts =
            calculateOfficeAndAdministrationCosts(
                budgetOptions?.officeAndAdministrationOnStaffCostsFlatRate,
                staffCosts
            )

        val finalOtherCosts =
            calculateOtherCosts(
                budgetOptions?.staffCostsFlatRate,
                budgetOptions?.otherCostsOnStaffCostsFlatRate,
                staffCosts
            )

        return BudgetCostsCalculationResult(
            staffCosts = finalStaffCosts,
            travelCosts = finalTravelCosts,
            officeAndAdministrationCosts = finalOfficeAndAdministrationCosts,
            otherCosts = finalOtherCosts,
            totalCosts = calculateTotalCosts(
                unitCosts,
                lumpSumsCosts,
                externalCosts,
                equipmentCosts,
                infrastructureCosts,
                finalTravelCosts,
                finalOfficeAndAdministrationCosts,
                finalOtherCosts,
                finalStaffCosts
            )
        )
    }


    private fun calculateStaffCosts(
        staffCostsFlatRate: Int,
        travelAndAccommodationOnStaffCostsFlatRate: Int?,
        externalCosts: BigDecimal,
        equipmentCosts: BigDecimal,
        infrastructureCosts: BigDecimal,
        travelCosts: BigDecimal
    ) =
        if (travelAndAccommodationOnStaffCostsFlatRate != null)
            externalCosts.add(equipmentCosts).add(infrastructureCosts).percentage(staffCostsFlatRate)
        else
            travelCosts.add(externalCosts).add(equipmentCosts).add(infrastructureCosts).percentage(staffCostsFlatRate)

    private fun calculateTravelCosts(travelAndAccommodationOnStaffCostsFlatRate: Int, staffCostTotal: BigDecimal, ) =
        staffCostTotal.percentage(travelAndAccommodationOnStaffCostsFlatRate)

    private fun calculateOfficeAndAdministrationCosts(
        officeAndAdministrationOnStaffCostsFlatRate: Int?,
        staffCostTotal: BigDecimal
    ): BigDecimal =
        if (officeAndAdministrationOnStaffCostsFlatRate != null)
            staffCostTotal.percentage(officeAndAdministrationOnStaffCostsFlatRate)
        else
            BigDecimal.ZERO

    private fun calculateOtherCosts(
        staffCostsFlatRate: Int?,
        otherCostsOnStaffCostsFlatRate: Int?,
        staffCostTotal: BigDecimal
    ): BigDecimal =
        if (otherCostsOnStaffCostsFlatRate != null) {
            if (staffCostsFlatRate == null)
                staffCostTotal.percentage(otherCostsOnStaffCostsFlatRate)
            else
                BigDecimal.ZERO
        } else
            BigDecimal.ZERO


    private fun calculateTotalCosts(
        unitCosts: BigDecimal,
        lumpSumsCosts: BigDecimal,
        externalCosts: BigDecimal,
        equipmentCosts: BigDecimal,
        infrastructureCosts: BigDecimal,
        travelCosts: BigDecimal,
        finalOfficeAndAdministrationCosts: BigDecimal,
        otherCosts: BigDecimal,
        staffCosts: BigDecimal
    ) =
        staffCosts
            .plus(travelCosts)
            .plus(externalCosts)
            .plus(equipmentCosts)
            .plus(infrastructureCosts)
            .plus(unitCosts)
            .plus(lumpSumsCosts)
            .plus(finalOfficeAndAdministrationCosts)
            .plus(otherCosts)

}
