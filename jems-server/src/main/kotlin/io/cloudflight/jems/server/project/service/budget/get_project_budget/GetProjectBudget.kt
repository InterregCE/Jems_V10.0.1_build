package io.cloudflight.jems.server.project.service.budget.get_project_budget

import io.cloudflight.jems.server.project.authorization.CanReadProject
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResult
import io.cloudflight.jems.server.project.service.budget.model.PartnerBudget
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerCost
import io.cloudflight.jems.server.project.service.common.BudgetCostsCalculatorService
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetProjectBudget(
    private val persistence: ProjectBudgetPersistence,
    private val optionPersistence: ProjectPartnerBudgetOptionsPersistence,
    private val lumpSumPersistence: ProjectLumpSumPersistence,
    private val budgetCostsCalculator: BudgetCostsCalculatorService
) : GetProjectBudgetInteractor {

    @Transactional(readOnly = true)
    @CanReadProject
    override fun getBudget(projectId: Long): List<PartnerBudget> {
        val partners = persistence.getPartnersForProjectId(projectId = projectId).associateBy { it.id!! }

        val options =
            optionPersistence.getBudgetOptions(partners.keys).iterator().asSequence().associateBy { it.partnerId }

        val lumpSumContributionPerPartner = persistence.getLumpSumContributionPerPartner(partners.keys)
        val unitCostsPerPartner = persistence.getUnitCostsPerPartner(partners.keys)

        val externalCostsPerPartner = persistence.getExternalCosts(partners.keys).groupByPartnerId()
        val equipmentCostsPerPartner = persistence.getEquipmentCosts(partners.keys).groupByPartnerId()
        val infrastructureCostsPerPartner = persistence.getInfrastructureCosts(partners.keys).groupByPartnerId()

        val staffCostsPerPartner =
            persistence.getStaffCosts(partners.filter { options[it.key]?.staffCostsFlatRate == null }.keys)
                .groupByPartnerId()
        val travelCostsPerPartner =
            persistence.getTravelCosts(partners.filter { options[it.key]?.travelAndAccommodationOnStaffCostsFlatRate == null }.keys)
                .groupByPartnerId()

        return partners.map { (partnerId, partner) ->
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
        partner: ProjectPartner?,
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
