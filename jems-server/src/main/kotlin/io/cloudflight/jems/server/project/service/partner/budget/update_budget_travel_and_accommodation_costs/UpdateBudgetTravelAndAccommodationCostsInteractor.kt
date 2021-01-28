package io.cloudflight.jems.server.project.service.partner.budget.update_budget_travel_and_accommodation_costs

import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry

interface UpdateBudgetTravelAndAccommodationCostsInteractor {
    fun updateBudgetTravelAndAccommodationCosts(partnerId: Long, travelCosts: List<BudgetTravelAndAccommodationCostEntry>): List<BudgetTravelAndAccommodationCostEntry>
}
