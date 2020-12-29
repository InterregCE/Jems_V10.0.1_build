package io.cloudflight.jems.server.project.service.partner.budget.get_budget_travel_and_accommodation_costs

import io.cloudflight.jems.server.project.authorization.CanReadProjectPartner
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetBudgetTravelAndAccommodationCosts(private val persistence: ProjectPartnerBudgetPersistence) : GetBudgetTravelAndAccommodationCostsInteractor {

    @Transactional(readOnly = true)
    @CanReadProjectPartner
    override fun getBudgetTravelAndAccommodationCosts(partnerId: Long) =
        persistence.getBudgetTravelAndAccommodationCosts(partnerId)

}
