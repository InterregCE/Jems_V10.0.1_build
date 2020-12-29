package io.cloudflight.jems.server.project.service.partner.budget.update_budge_staff_costs

import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetPersistence
import io.cloudflight.jems.server.project.service.partner.budget.validateBudgetEntries
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.truncateNumbers
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateBudgetStaffCosts(private val persistence: ProjectPartnerBudgetPersistence) : UpdateBudgetStaffCostsInteractor {

    @Transactional
    @CanUpdateProjectPartner
    override fun updateBudgetStaffCosts(partnerId: Long, staffCosts: List<BudgetStaffCostEntry>): List<BudgetStaffCostEntry> {

        validateBudgetEntries(staffCosts)

        persistence.deleteAllBudgetStaffCostsExceptFor(
            partnerId = partnerId,
            idsToKeep = staffCosts.filter { it.id !== null }.map { it.id!! }
        )

        return persistence.createOrUpdateBudgetStaffCosts(partnerId, staffCosts.map { it.apply { this.truncateNumbers() } })
    }

}
