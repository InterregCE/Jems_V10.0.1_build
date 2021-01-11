package io.cloudflight.jems.api.project.dto.partner.budget

import io.swagger.annotations.ApiModel

@ApiModel(value = "BudgetCostsDTO")
data class BudgetCostsDTO(
    val staffCosts: List<BudgetStaffCostEntryDTO>,
    val travelCosts: List<BudgetTravelAndAccommodationCostEntryDTO>,
    val externalCosts: List<BudgetGeneralCostEntryDTO>,
    val equipmentCosts: List<BudgetGeneralCostEntryDTO>,
    val infrastructureCosts: List<BudgetGeneralCostEntryDTO>,
)
