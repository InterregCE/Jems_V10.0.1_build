package io.cloudflight.jems.server.project.repository.budget

import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetEquipmentRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetExternalRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetInfrastructureRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetStaffCostRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetTravelRepository
import io.cloudflight.jems.server.project.repository.partner.toProjectPartner
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerCost
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Repository
class ProjectBudgetPersistenceProvider(
    private val projectPartnerRepository: ProjectPartnerRepository,
    private val budgetStaffCostRepository: ProjectPartnerBudgetStaffCostRepository,
    private val budgetTravelRepository: ProjectPartnerBudgetTravelRepository,
    private val budgetExternalRepository: ProjectPartnerBudgetExternalRepository,
    private val budgetEquipmentRepository: ProjectPartnerBudgetEquipmentRepository,
    private val budgetInfrastructureRepository: ProjectPartnerBudgetInfrastructureRepository,
    private val projectLumpSumRepository: ProjectLumpSumRepository,
) : ProjectBudgetPersistence {

    @Transactional(readOnly = true)
    override fun getStaffCosts(partnerIds: Set<Long>): List<ProjectPartnerCost> =
        budgetStaffCostRepository.sumForAllPartners(partnerIds).toProjectPartnerBudget()

    @Transactional(readOnly = true)
    override fun getTravelCosts(partnerIds: Set<Long>): List<ProjectPartnerCost> =
        budgetTravelRepository.sumForAllPartners(partnerIds).toProjectPartnerBudget()

    @Transactional(readOnly = true)
    override fun getExternalCosts(partnerIds: Set<Long>): List<ProjectPartnerCost> =
        budgetExternalRepository.sumForAllPartners(partnerIds).toProjectPartnerBudget()

    @Transactional(readOnly = true)
    override fun getEquipmentCosts(partnerIds: Set<Long>): List<ProjectPartnerCost> =
        budgetEquipmentRepository.sumForAllPartners(partnerIds).toProjectPartnerBudget()

    @Transactional(readOnly = true)
    override fun getInfrastructureCosts(partnerIds: Set<Long>): List<ProjectPartnerCost> =
        budgetInfrastructureRepository.sumForAllPartners(partnerIds).toProjectPartnerBudget()

    @Transactional(readOnly = true)
    override fun getLumpSumContributionPerPartner(lumpSumIds: Set<UUID>): Map<Long, BigDecimal> =
        projectLumpSumRepository.sumLumpSumsPerPartner(lumpSumIds).associateBy({ it.partner.id }, { it.sum })

    @Transactional(readOnly = true)
    override fun getPartnersForProjectId(projectId: Long): List<ProjectPartner> =
        projectPartnerRepository.findTop30ByProjectId(projectId, Sort.unsorted()).toProjectPartner()

}
