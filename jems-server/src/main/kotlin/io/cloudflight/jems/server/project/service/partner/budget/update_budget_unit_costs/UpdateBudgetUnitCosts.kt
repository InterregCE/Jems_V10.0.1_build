package io.cloudflight.jems.server.project.service.partner.budget.update_budget_unit_costs

import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerService
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetPersistence
import io.cloudflight.jems.server.project.service.partner.budget.truncate
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateBudgetUnitCosts(
    private val persistence: ProjectPartnerBudgetPersistence,
    private val projectPersistence: ProjectPersistence,
    private val projectPartnerService: ProjectPartnerService,
    private val budgetCostEntriesValidator: BudgetUnitCostEntriesValidator) : UpdateBudgetUnitCostsInteractor {

    @Transactional
    @CanUpdateProjectPartner
    override fun updateBudgetUnitCosts(partnerId: Long, unitCosts: List<BudgetUnitCostEntry>): List<BudgetUnitCostEntry> {
        val projectUnitCosts = projectPersistence
            .getProjectCallSettingsForProject(projectPartnerService.getProjectIdForPartnerId(partnerId)).unitCosts

        budgetCostEntriesValidator.validate(unitCosts, projectUnitCosts)

        persistence.deleteAllUnitCostsExceptFor(
            partnerId = partnerId,
            idsToKeep = unitCosts.filter { it.id !== null }.map { it.id!! }
        )

        return persistence.createOrUpdateBudgetUnitCosts(partnerId, unitCosts.map { it.apply { this.truncateNumbers() } })
    }

    fun BudgetUnitCostEntry.truncateNumbers() = this.apply {
        numberOfUnits.truncate()
        rowSum.truncate()
    }
}
