package io.cloudflight.jems.server.project.service.partner.budget.get_budget_options

import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectPartner
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetBudgetOptions(private val persistence: ProjectPartnerBudgetOptionsPersistence) : GetBudgetOptionsInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProjectPartner
    override fun getBudgetOptions(partnerId: Long, version: String?) =
        persistence.getBudgetOptions(partnerId, version)
}
