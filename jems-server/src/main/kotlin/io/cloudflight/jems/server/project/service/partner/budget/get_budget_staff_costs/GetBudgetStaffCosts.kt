package io.cloudflight.jems.server.project.service.partner.budget.get_budget_staff_costs

import io.cloudflight.jems.server.project.authorization.CanReadProjectPartner
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetBudgetStaffCosts(private val persistence: ProjectPartnerBudgetPersistence) : GetBudgetStaffCostsInteractor {

    @Transactional(readOnly = true)
    @CanReadProjectPartner
    override fun getBudgetStaffCosts(partnerId: Long) =
        persistence.getBudgetStaffCosts(partnerId)

}
