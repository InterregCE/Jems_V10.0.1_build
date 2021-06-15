package io.cloudflight.jems.server.project.repository.budget

import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetEquipmentRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetExternalRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetInfrastructureRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetStaffCostRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetTravelRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetUnitCostRepository
import io.cloudflight.jems.server.project.repository.partner.toProjectPartner
import io.cloudflight.jems.server.project.repository.partner.toProjectPartnerHistoricalData
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerCost
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import java.math.BigDecimal
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectBudgetPersistenceProvider(
    private val projectPartnerRepository: ProjectPartnerRepository,
    private val budgetStaffCostRepository: ProjectPartnerBudgetStaffCostRepository,
    private val budgetTravelRepository: ProjectPartnerBudgetTravelRepository,
    private val budgetExternalRepository: ProjectPartnerBudgetExternalRepository,
    private val budgetEquipmentRepository: ProjectPartnerBudgetEquipmentRepository,
    private val budgetInfrastructureRepository: ProjectPartnerBudgetInfrastructureRepository,
    private val budgetUnitCostRepository: ProjectPartnerBudgetUnitCostRepository,
    private val projectPartnerLumpSumRepository: ProjectPartnerLumpSumRepository,
    private val projectVersionUtils: ProjectVersionUtils
) : ProjectBudgetPersistence {

    @Transactional(readOnly = true)
    override fun getStaffCosts(partnerIds: Set<Long>, projectId: Long, version: String?): List<ProjectPartnerCost> =
        projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                budgetStaffCostRepository.sumForAllPartners(partnerIds).toProjectPartnerBudget()
            },
            previousVersionFetcher = { timestamp ->
                budgetStaffCostRepository.sumForAllPartnersAsOfTimestamp(partnerIds, timestamp).toProjectPartnerBudgetHistoricalData()
            }) ?: emptyList()

    @Transactional(readOnly = true)
    override fun getTravelCosts(partnerIds: Set<Long>, projectId: Long, version: String?): List<ProjectPartnerCost> =
        projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                budgetTravelRepository.sumForAllPartners(partnerIds).toProjectPartnerBudget()
            },
            previousVersionFetcher = { timestamp ->
                budgetTravelRepository.sumForAllPartnersAsOfTimestamp(partnerIds, timestamp).toProjectPartnerBudgetHistoricalData()
            }) ?: emptyList()

    @Transactional(readOnly = true)
    override fun getExternalCosts(partnerIds: Set<Long>, projectId: Long, version: String?): List<ProjectPartnerCost> =
        projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                budgetExternalRepository.sumForAllPartners(partnerIds).toProjectPartnerBudget()
            },
            previousVersionFetcher = { timestamp ->
                budgetExternalRepository.sumForAllPartnersAsOfTimestamp(partnerIds, timestamp).toProjectPartnerBudgetHistoricalData()
            }) ?: emptyList()

    @Transactional(readOnly = true)
    override fun getEquipmentCosts(partnerIds: Set<Long>, projectId: Long, version: String?): List<ProjectPartnerCost> =
        projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                budgetEquipmentRepository.sumForAllPartners(partnerIds).toProjectPartnerBudget()
            },
            previousVersionFetcher = { timestamp ->
                budgetEquipmentRepository.sumForAllPartnersAsOfTimestamp(partnerIds, timestamp).toProjectPartnerBudgetHistoricalData()
            }) ?: emptyList()

    @Transactional(readOnly = true)
    override fun getInfrastructureCosts(
        partnerIds: Set<Long>,
        projectId: Long,
        version: String?
    ): List<ProjectPartnerCost> =
        projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                budgetInfrastructureRepository.sumForAllPartners(partnerIds).toProjectPartnerBudget()
            },
            previousVersionFetcher = { timestamp ->
                budgetInfrastructureRepository.sumForAllPartnersAsOfTimestamp(partnerIds, timestamp).toProjectPartnerBudgetHistoricalData()
            }) ?: emptyList()

    @Transactional(readOnly = true)
    override fun getLumpSumContributionPerPartner(
        partnerIds: Set<Long>,
        projectId: Long,
        version: String?
    ): Map<Long, BigDecimal> =
        projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                projectPartnerLumpSumRepository.sumLumpSumsPerPartner(partnerIds)
                    .associateBy({ it.partner.id }, { it.sum })
            },
            previousVersionFetcher = { timestamp ->
                projectPartnerLumpSumRepository.sumLumpSumsPerPartnerAsOfTimestamp(partnerIds, timestamp)
                    .associateBy({ it.partnerId }, { it.sum })
            }) ?: emptyMap()

    @Transactional(readOnly = true)
    override fun getUnitCostsPerPartner(
        partnerIds: Set<Long>,
        projectId: Long,
        version: String?
    ): Map<Long, BigDecimal> =
        projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                budgetUnitCostRepository.sumForAllPartners(partnerIds).associateBy({ it.partnerId }, { it.sum })
            },
            previousVersionFetcher = { timestamp ->
                budgetUnitCostRepository.sumForAllPartnersAsOfTimestamp(partnerIds, timestamp)
                    .associateBy({ it.partnerId }, { it.sum })
            }) ?: emptyMap()

    @Transactional(readOnly = true)
    override fun getPartnersForProjectId(projectId: Long, version: String?): List<ProjectPartner> =
        projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                projectPartnerRepository.findTop30ByProjectId(projectId, Sort.unsorted()).toProjectPartner()
            },
            previousVersionFetcher = { timestamp ->
                projectPartnerRepository.findTop30ByProjectIdSortBySortNumberAsOfTimestamp(projectId, timestamp)
                    .map { it.toProjectPartnerHistoricalData() }
            }) ?: emptyList()
}
