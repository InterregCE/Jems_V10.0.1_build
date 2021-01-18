package io.cloudflight.jems.server.project.service.partner.budget.update_budget_unit_costs

import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerService
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
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
    private val budgetCostEntriesValidator: BudgetUnitCostEntriesValidator,
    private val budgetOptionsPersistence: ProjectPartnerBudgetOptionsPersistence,
) : UpdateBudgetUnitCostsInteractor {

    @Transactional
    @CanUpdateProjectPartner
    override fun updateBudgetUnitCosts(partnerId: Long, unitCosts: List<BudgetUnitCostEntry>): List<BudgetUnitCostEntry> {
        if (budgetOptionsPersistence.getBudgetOptions(partnerId)?.otherCostsOnStaffCostsFlatRate != null)
            throw I18nValidationException(i18nKey = "project.partner.budget.not.allowed.because.of.otherCostsOnStaffCostsFlatRate")

        val projectUnitCosts = projectPersistence
            .getProjectCallSettingsForProject(projectPartnerService.getProjectIdForPartnerId(partnerId)).unitCosts

        budgetCostEntriesValidator.validate(unitCosts, projectUnitCosts)

        persistence.deleteAllUnitCostsExceptFor(
            partnerId = partnerId,
            idsToKeep = unitCosts.mapNotNullTo(HashSet()) { it.id }
        )

        return persistence.createOrUpdateBudgetUnitCosts(partnerId, unitCosts)
    }

}
