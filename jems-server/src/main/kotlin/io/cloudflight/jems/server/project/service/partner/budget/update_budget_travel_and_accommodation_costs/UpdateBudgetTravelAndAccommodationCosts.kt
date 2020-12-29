package io.cloudflight.jems.server.project.service.partner.budget.update_budget_travel_and_accommodation_costs

import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetPersistence
import io.cloudflight.jems.server.project.service.partner.budget.validateBudgetEntries
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry
import io.cloudflight.jems.server.project.service.partner.model.truncateNumbers
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateBudgetTravelAndAccommodationCosts(private val persistence: ProjectPartnerBudgetPersistence) : UpdateBudgetTravelAndAccommodationCostsInteractor {

    @Transactional
    @CanUpdateProjectPartner
    override fun updateBudgetTravelAndAccommodationCosts(partnerId: Long, travelAndAccommodationCosts: List<BudgetTravelAndAccommodationCostEntry>): List<BudgetTravelAndAccommodationCostEntry> {
        validateBudgetEntries(travelAndAccommodationCosts)

        persistence.deleteAllBudgetTravelAndAccommodationCostsExceptFor(
            partnerId = partnerId,
            idsToKeep = travelAndAccommodationCosts.filter { it.id !== null }.map { it.id!! }
        )

        return persistence.createOrUpdateBudgetTravelAndAccommodationCosts(partnerId, travelAndAccommodationCosts.map { it.apply { this.truncateNumbers() }})
    }

}
