package io.cloudflight.jems.server.project.service.budget.get_project_budget

import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResult
import io.cloudflight.jems.server.project.service.budget.model.PartnerBudget
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerCost
import io.cloudflight.jems.server.project.service.common.BudgetCostsCalculatorService
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetProjectBudget(
    private val persistence: ProjectBudgetPersistence,
    private val optionPersistence: ProjectPartnerBudgetOptionsPersistence,
    private val budgetCostsCalculator: BudgetCostsCalculatorService
) : GetProjectBudgetInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProjectForm
    override fun getBudget(projectId: Long, version: String?): List<PartnerBudget> {
        val partners = persistence.getPartnersForProjectId(projectId = projectId, version)
        return getBudget(partners, projectId, version)
    }

    @Transactional(readOnly = true)
    override fun getBudget(partners: List<ProjectPartnerSummary>, projectId: Long, version: String?): List<PartnerBudget> {
        val partnersById = partners.associateBy { it.id!! }

        val options =
            optionPersistence.getBudgetOptions(partnersById.keys, projectId, version).iterator().asSequence().associateBy { it.partnerId }

        val lumpSumContributionPerPartner = persistence.getLumpSumContributionPerPartner(partnersById.keys, projectId, version)
        val unitCostsPerPartner = persistence.getUnitCostsPerPartner(partnersById.keys, projectId, version)

        val externalCostsPerPartner = persistence.getExternalCosts(partnersById.keys, projectId, version).groupByPartnerId()
        val equipmentCostsPerPartner = persistence.getEquipmentCosts(partnersById.keys, projectId, version).groupByPartnerId()
        val infrastructureCostsPerPartner = persistence.getInfrastructureCosts(partnersById.keys, projectId, version).groupByPartnerId()

        val staffCostsPerPartner =
            persistence.getStaffCosts(partnersById.filter { options[it.key]?.staffCostsFlatRate == null }.keys, projectId, version)
                .groupByPartnerId()
        val travelCostsPerPartner =
            persistence.getTravelCosts(partnersById.filter { options[it.key]?.travelAndAccommodationOnStaffCostsFlatRate == null }.keys, projectId, version)
                .groupByPartnerId()

        return partnersById.map { (partnerId, partner) ->
            val unitCosts = unitCostsPerPartner[partnerId] ?: BigDecimal.ZERO
            val lumpSumsCosts = lumpSumContributionPerPartner[partnerId] ?: BigDecimal.ZERO
            val externalCosts = externalCostsPerPartner[partnerId] ?: BigDecimal.ZERO
            val equipmentCosts = equipmentCostsPerPartner[partnerId] ?: BigDecimal.ZERO
            val infrastructureCosts = infrastructureCostsPerPartner[partnerId] ?: BigDecimal.ZERO
            budgetCostsCalculator.calculateCosts(
                options[partnerId],
                unitCosts = unitCosts,
                lumpSumsCosts = lumpSumsCosts,
                externalCosts = externalCosts,
                equipmentCosts = equipmentCosts,
                infrastructureCosts = infrastructureCosts,
                travelCosts = travelCostsPerPartner[partnerId] ?: BigDecimal.ZERO,
                staffCosts = staffCostsPerPartner[partnerId] ?: BigDecimal.ZERO,
            ).toPartnerBudget(
                partner,
                unitCosts = unitCosts,
                lumpSumCosts = lumpSumsCosts,
                externalCosts = externalCosts,
                equipmentCosts = equipmentCosts,
                infrastructureCosts = infrastructureCosts,
            )
        }
    }

    private fun Collection<ProjectPartnerCost>.groupByPartnerId() = associateBy({ it.partnerId }, { it.sum })

    private fun BudgetCostsCalculationResult.toPartnerBudget(
        partner: ProjectPartnerSummary,
        unitCosts: BigDecimal,
        lumpSumCosts: BigDecimal,
        externalCosts: BigDecimal,
        equipmentCosts: BigDecimal,
        infrastructureCosts: BigDecimal
    ) =
        PartnerBudget(
            partner = partner,
            staffCosts = this.staffCosts,
            travelCosts = this.travelCosts,
            externalCosts = externalCosts,
            equipmentCosts = equipmentCosts,
            infrastructureCosts = infrastructureCosts,
            officeAndAdministrationCosts = this.officeAndAdministrationCosts,
            otherCosts = this.otherCosts,
            lumpSumContribution = lumpSumCosts,
            unitCosts = unitCosts,
            totalCosts = this.totalCosts
        )
}
