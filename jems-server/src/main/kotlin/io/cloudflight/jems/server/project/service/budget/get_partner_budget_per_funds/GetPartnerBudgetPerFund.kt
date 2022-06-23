package io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_funds

import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.repository.partner.cofinancing.ProjectPartnerCoFinancingPersistenceProvider
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.cofinancing.model.PartnerBudgetCoFinancing
import io.cloudflight.jems.server.project.service.cofinancing.model.PartnerBudgetSpfCoFinancing
import io.cloudflight.jems.server.project.service.common.PartnerBudgetPerFundCalculatorService
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCost
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
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
        val call = callPersistence.getCallByProjectId(projectId)
        val projectChosenFunds = call.funds.map { it.programmeFund }

        val budgetCoFinancingContributions: MutableMap<Long, ProjectPartnerCoFinancingAndContribution> = mutableMapOf()
        partners.forEach { partner ->
            budgetCoFinancingContributions[partner.id!!] =
                projectPartnerCoFinancingPersistence.getCoFinancingAndContributions(partner.id, version)
        }

        val coFinancing = partners.map { partner ->
            PartnerBudgetCoFinancing(
                partner = partner,
                projectPartnerCoFinancingAndContribution = budgetCoFinancingContributions[partner.id],
                total = getBudgetTotalCost.getBudgetTotalCost(partner.id!!, version)
            )
        }
        val spfCoFinancing = getSpfCoFinancing(call.type, partners, version)

        return partnerBudgetPerFundCalculator.calculate(
            partners, projectChosenFunds, coFinancing, spfCoFinancing
        )
    }

    private fun getSpfCoFinancing(
        callType: CallType,
        partners: List<ProjectPartnerSummary>,
        version: String?
    ): List<PartnerBudgetSpfCoFinancing?> {
        return if (callType == CallType.SPF) {
            partners.map {
                if (it.id != null)
                    PartnerBudgetSpfCoFinancing(
                        partner = it,
                        projectPartnerCoFinancingAndContribution =
                        projectPartnerCoFinancingPersistence.getSpfCoFinancingAndContributions(it.id, version),
                        total = getBudgetTotalCost.getBudgetTotalSpfCost(it.id, version)
                    )
                else
                    null
            }
        } else emptyList()
    }

}
