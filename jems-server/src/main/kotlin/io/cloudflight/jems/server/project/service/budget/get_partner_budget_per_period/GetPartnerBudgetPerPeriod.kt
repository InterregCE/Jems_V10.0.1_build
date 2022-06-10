package io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period

import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.budget.model.PartnersAggregatedInfo
import io.cloudflight.jems.server.project.service.budget.model.ProjectSpfBudgetPerPeriod
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.model.ProjectBudgetOverviewPerPartnerPerPeriod
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

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
        val spfBeneficiary = if (callDetail.type == CallType.SPF)
            projectPartners.firstOrNull { it.active && it.role == ProjectPartnerRole.LEAD_PARTNER }
        else
            null

        return projectPartners.let { partners ->
            val partnerIds = partners.mapNotNullTo(HashSet()) { it.id }
            calculatePartnerBudgetPerPeriod.calculate(
                PartnersAggregatedInfo(
                    partners, optionPersistence.getBudgetOptions(partnerIds, projectId, version),
                    persistence.getBudgetPerPartner(partnerIds, projectId, version),
                    persistence.getBudgetTotalForPartners(partnerIds, projectId, version)
                ),
                lumpSums = lumpSumPersistence.getLumpSums(projectId, version),
                projectPeriods = projectPersistence.getProjectPeriods(projectId, version),
                spfBudgetPerPeriod = getSpfBudgetPerPeriod(projectId, version, callDetail.type, spfBeneficiary),
                spfTotalBudget = getSpfBudgetTotal(version, callDetail.type, spfBeneficiary),
                spfBeneficiary = spfBeneficiary
            )
        }
    }

    private fun getSpfBudgetPerPeriod(
        projectId: Long,
        version: String?,
        callType: CallType,
        spfBeneficiary: ProjectPartnerSummary?
    ): List<ProjectSpfBudgetPerPeriod> {
        if (callType == CallType.SPF && spfBeneficiary?.id != null) {
            return persistence.getSpfBudgetPerPeriod(spfBeneficiary.id, projectId, version).toMutableList()
        }
        return emptyList()
    }

    private fun getSpfBudgetTotal(
        version: String?,
        callType: CallType,
        spfBeneficiary: ProjectPartnerSummary?
    ): BigDecimal {
        if (callType == CallType.SPF && spfBeneficiary?.id != null) {
            return budgetCostsPersistence.getBudgetSpfCostTotal(spfBeneficiary.id, version)
        }
        return BigDecimal.ZERO
    }
}
