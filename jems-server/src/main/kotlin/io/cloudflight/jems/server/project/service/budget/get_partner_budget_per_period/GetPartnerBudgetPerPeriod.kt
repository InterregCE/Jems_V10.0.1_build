package io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period

import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.budget.model.PartnersAggregatedInfo
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.model.ProjectBudgetOverviewPerPartnerPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPartnerBudgetPerPeriod(
    private val persistence: ProjectBudgetPersistence,
    private val optionPersistence: ProjectPartnerBudgetOptionsPersistence,
    private val projectPersistence: ProjectPersistence,
    private val lumpSumPersistence: ProjectLumpSumPersistence,
    private val calculatePartnerBudgetPerPeriod: PartnerBudgetPerPeriodCalculatorService,
    private val budgetCostsPersistence: ProjectPartnerBudgetCostsPersistence,
    private val callPersistence: CallPersistence,
) : GetPartnerBudgetPerPeriodInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProjectForm
    @ExceptionWrapper(GetPartnerBudgetPerPeriodException::class)
    override fun getPartnerBudgetPerPeriod(
        projectId: Long, version: String?
    ): ProjectBudgetOverviewPerPartnerPerPeriod {

        val callDetail = callPersistence.getCallByProjectId(projectId)
        val projectPartners = persistence.getPartnersForProjectId(projectId, version)
        val projectPeriods = projectPersistence.getProjectPeriods(projectId, version)

        val spfPartnersBudgetPerPeriod =
            if (callDetail.type == CallType.SPF) {
                projectPartners.map {
                    getSpfPartnerBudgetPerPeriod(
                        partnerSummary = it,
                        projectPeriods,
                        projectId,
                        version
                    )
                }
            }
            else
                emptyList()

        return projectPartners.let { partners ->
            val partnerIds = partners.mapNotNullTo(HashSet()) { it.id }
            calculatePartnerBudgetPerPeriod.calculate(
                PartnersAggregatedInfo(
                    partners, optionPersistence.getBudgetOptions(partnerIds, projectId, version),
                    persistence.getBudgetPerPartner(partnerIds, projectId, version),
                    persistence.getBudgetTotalForPartners(partnerIds, projectId, version)
                ),
                lumpSums = lumpSumPersistence.getLumpSums(projectId, version),
                projectPeriods = projectPeriods,
                spfPartnerBudgetPerPeriod = spfPartnersBudgetPerPeriod.flatten()
            )
        }
    }

    private fun getSpfPartnerBudgetPerPeriod(
        partnerSummary: ProjectPartnerSummary?,
        projectPeriods: List<ProjectPeriod>,
        projectId: Long,
        version: String?
    ): List<ProjectPartnerBudgetPerPeriod> {

        return if (partnerSummary?.id != null) {
            calculatePartnerBudgetPerPeriod.calculateSpfPartnerBudgetPerPeriod(
                spfBeneficiary = partnerSummary,
                projectPeriods = projectPeriods,
                spfBudgetPerPeriod = persistence.getSpfBudgetPerPeriod(partnerSummary.id, projectId, version).toMutableList(),
                spfTotalBudget = budgetCostsPersistence.getBudgetSpfCostTotal(partnerSummary.id, version)
            )
        } else {
            emptyList()
        }
    }

}
