package io.cloudflight.jems.server.project.service.partner.budget.get_budget_travel_and_accommodation_costs

import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry

interface GetBudgetTravelAndAccommodationCostsInteractor {
    fun getBudgetTravelAndAccommodationCosts(partnerId: Long): List<BudgetTravelAndAccommodationCostEntry>
}
