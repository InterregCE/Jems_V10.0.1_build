package io.cloudflight.jems.server.project.service.partner.budget.get_budget_options

import io.cloudflight.jems.server.project.authorization.CanReadProjectPartner
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetBudgetOptions(private val persistence: ProjectPartnerBudgetOptionsPersistence) : GetBudgetOptionsInteractor {

    @Transactional(readOnly = true)
    @CanReadProjectPartner
    override fun getBudgetOptions(partnerId: Long) =
        persistence.getBudgetOptions(partnerId)
}
