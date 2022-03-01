package io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.budget.model.PartnersAggregatedInfo
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.model.ProjectBudgetOverviewPerPartnerPerPeriod
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPartnerBudgetPerPeriod(
    private val persistence: ProjectBudgetPersistence,
    private val optionPersistence: ProjectPartnerBudgetOptionsPersistence,
    private val projectPersistence: ProjectPersistence,
    private val lumpSumPersistence: ProjectLumpSumPersistence,
    private val calculatePartnerBudgetPerPeriod: PartnerBudgetPerPeriodCalculatorService
) : GetPartnerBudgetPerPeriodInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProjectForm
    @ExceptionWrapper(GetPartnerBudgetPerPeriodException::class)
    override fun getPartnerBudgetPerPeriod(
        projectId: Long, version: String?
    ): ProjectBudgetOverviewPerPartnerPerPeriod =
        persistence.getPartnersForProjectId(projectId = projectId, version).let { partners ->
            val partnerIds = partners.mapNotNullTo(HashSet()) { it.id }
            calculatePartnerBudgetPerPeriod.calculate(
                PartnersAggregatedInfo(
                    partners, optionPersistence.getBudgetOptions(partnerIds, projectId, version),
                    persistence.getBudgetPerPartner(partnerIds, projectId, version),
                    persistence.getBudgetTotalForPartners(partnerIds, projectId, version)
                ),
                lumpSums = lumpSumPersistence.getLumpSums(projectId, version),
                projectPeriods = projectPersistence.getProjectPeriods(projectId, version),
                )
        }
}
