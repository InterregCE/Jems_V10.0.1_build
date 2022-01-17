package io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_funds

import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.repository.partner.cofinancing.ProjectPartnerCoFinancingPersistenceProvider
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.cofinancing.model.PartnerBudgetCoFinancing
import io.cloudflight.jems.server.project.service.common.PartnerBudgetPerFundCalculatorService
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCost
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPartnerBudgetPerFund(
    private val callPersistence: CallPersistence,
    private val projectBudgetPersistence: ProjectBudgetPersistence,
    private val projectPartnerCoFinancingPersistence: ProjectPartnerCoFinancingPersistenceProvider,
    private val getBudgetTotalCost: GetBudgetTotalCost,
    private val partnerBudgetPerFundCalculator: PartnerBudgetPerFundCalculatorService
) : GetPartnerBudgetPerFundInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProjectForm
    @ExceptionWrapper(GetPartnerBudgetPerFundExceptions::class)
    override fun getProjectPartnerBudgetPerFund(projectId: Long, version: String?): List<ProjectPartnerBudgetPerFund> {
        val partners = projectBudgetPersistence.getPartnersForProjectId(projectId = projectId, version)
        val projectChosenFunds = callPersistence.getCallByProjectId(projectId).funds.map { it.programmeFund }

        val budgetCoFinancingContributions: MutableMap<Long, ProjectPartnerCoFinancingAndContribution> = mutableMapOf()
        partners.forEach { partner ->
            budgetCoFinancingContributions[partner.id!!] =
                projectPartnerCoFinancingPersistence.getCoFinancingAndContributions(partner.id, version)
        }

        val coFinancing = partners.map { partner ->
            PartnerBudgetCoFinancing(
                partner = partner,
                budgetCoFinancingContributions[partner.id],
                total = getBudgetTotalCost.getBudgetTotalCost(partner.id!!, version)
            )
        }

        return partnerBudgetPerFundCalculator.calculate(
            partners, projectChosenFunds, coFinancing
        )
    }
}
