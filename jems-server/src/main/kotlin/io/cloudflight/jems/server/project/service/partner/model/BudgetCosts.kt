package io.cloudflight.jems.server.project.service.partner.model

data class BudgetCosts(
    val staffCosts: List<BudgetStaffCostEntry>,
    val travelCosts: List<BudgetTravelAndAccommodationCostEntry>,
    val externalCosts: List<BudgetGeneralCostEntry>,
    val equipmentCosts: List<BudgetGeneralCostEntry>,
    val infrastructureCosts: List<BudgetGeneralCostEntry>,
    val unitCosts: List<BudgetUnitCostEntry>,
    val spfCosts: List<BudgetSpfCostEntry>
)
