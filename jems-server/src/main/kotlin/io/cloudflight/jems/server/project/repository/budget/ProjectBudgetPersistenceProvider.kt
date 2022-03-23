package io.cloudflight.jems.server.project.repository.budget

import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetEquipmentRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetExternalRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetInfrastructureRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetStaffCostRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetTravelRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetUnitCostRepository
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toProjectUnitCosts
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toProjectUnitCostsGrouped
import io.cloudflight.jems.server.project.repository.partner.toProjectPartner
import io.cloudflight.jems.server.project.repository.partner.toProjectPartnerBudgetPerPeriod
import io.cloudflight.jems.server.project.repository.partner.toProjectPartnerHistoricalData
import io.cloudflight.jems.server.project.repository.partner.toProjectPartnerSpfBudgetPerPeriod
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerBudget
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerCost
import io.cloudflight.jems.server.project.service.budget.model.ProjectSpfBudgetPerPeriod
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.partner.model.PartnerTotalBudgetPerCostCategory
import io.cloudflight.jems.server.project.service.unitcost.model.ProjectUnitCost
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
                budgetStaffCostRepository.sumForAllPartnersAsOfTimestamp(partnerIds, timestamp)
                    .toProjectPartnerBudgetHistoricalData()
            }) ?: emptyList()

    @Transactional(readOnly = true)
    override fun getTravelCosts(partnerIds: Set<Long>, projectId: Long, version: String?): List<ProjectPartnerCost> =
        projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                budgetTravelRepository.sumForAllPartners(partnerIds).toProjectPartnerBudget()
            },
            previousVersionFetcher = { timestamp ->
                budgetTravelRepository.sumForAllPartnersAsOfTimestamp(partnerIds, timestamp)
                    .toProjectPartnerBudgetHistoricalData()
            }) ?: emptyList()

    @Transactional(readOnly = true)
    override fun getExternalCosts(partnerIds: Set<Long>, projectId: Long, version: String?): List<ProjectPartnerCost> =
        projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                budgetExternalRepository.sumForAllPartners(partnerIds).toProjectPartnerBudget()
            },
            previousVersionFetcher = { timestamp ->
                budgetExternalRepository.sumForAllPartnersAsOfTimestamp(partnerIds, timestamp)
                    .toProjectPartnerBudgetHistoricalData()
            }) ?: emptyList()

    @Transactional(readOnly = true)
    override fun getEquipmentCosts(partnerIds: Set<Long>, projectId: Long, version: String?): List<ProjectPartnerCost> =
        projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                budgetEquipmentRepository.sumForAllPartners(partnerIds).toProjectPartnerBudget()
            },
            previousVersionFetcher = { timestamp ->
                budgetEquipmentRepository.sumForAllPartnersAsOfTimestamp(partnerIds, timestamp)
                    .toProjectPartnerBudgetHistoricalData()
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
                budgetInfrastructureRepository.sumForAllPartnersAsOfTimestamp(partnerIds, timestamp)
                    .toProjectPartnerBudgetHistoricalData()
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
    override fun getPartnersForProjectId(projectId: Long, version: String?): List<ProjectPartnerSummary> =
        projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                projectPartnerRepository.findTop30ByProjectId(
                    projectId, Sort.by(
                        Sort.Order(Sort.Direction.ASC, "sortNumber"),
                    )
                ).toProjectPartner()
            },
            previousVersionFetcher = { timestamp ->
                projectPartnerRepository.findTop30ByProjectIdSortBySortNumberAsOfTimestamp(projectId, timestamp)
                    .map { it.toProjectPartnerHistoricalData() }
            }) ?: emptyList()

    @Transactional(readOnly = true)
    override fun getBudgetPerPartner(
        partnerIds: Set<Long>,
        projectId: Long,
        version: String?
    ): List<ProjectPartnerBudget> =
        projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                projectPartnerRepository.getAllBudgetsByIds(partnerIds).toProjectPartnerBudgetPerPeriod()
            },
            previousVersionFetcher = { timestamp ->
                projectPartnerRepository.getAllBudgetsByPartnerIdsAsOfTimestamp(partnerIds, timestamp)
                    .toProjectPartnerBudgetPerPeriod()
            }
        ) ?: emptyList()

    @Transactional(readOnly = true)
    override fun getBudgetTotalForPartners(
        partnerIds: Set<Long>,
        projectId: Long,
        version: String?
    ): Map<Long, PartnerTotalBudgetPerCostCategory> =
        projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                projectPartnerRepository.getAllPartnerTotalBudgetData(partnerIds)
                    .associateBy({ it.partnerId }, { it.toModel() })
            },
            previousVersionFetcher = { timestamp ->
                projectPartnerRepository.getAllPartnerTotalBudgetDataAsOfTimestamp(partnerIds, timestamp)
                    .associateBy({ it.partnerId }, { it.toModel() })
            }) ?: emptyMap()

    @Transactional(readOnly = true)
    override fun getProjectUnitCosts(projectId: Long, version: String?): List<ProjectUnitCost> =
        projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                budgetUnitCostRepository.findProjectUnitCosts(
                    projectPartnerRepository.findTop30ByProjectId(projectId).map { it.id }.toSet()
                ).toProjectUnitCosts().toProjectUnitCostsGrouped()
            },
            previousVersionFetcher = { timestamp ->
                budgetUnitCostRepository.findProjectUnitCostsAsOfTimestamp(
                    projectPartnerRepository.findTop30ByProjectIdSortBySortNumberAsOfTimestamp(
                        projectId,
                        timestamp
                    ).map { it.id }.toSet(), timestamp
                ).toProjectUnitCosts()
                    .toProjectUnitCostsGrouped()
            }
        ) ?: emptyList()

    @Transactional(readOnly = true)
    override fun getSpfBudgetPerPeriod(partnerId: Long, projectId: Long, version: String?): List<ProjectSpfBudgetPerPeriod> =
        projectVersionUtils.fetch(
            version, projectId,
            currentVersionFetcher = {
                projectPartnerRepository.getSpfBudgetByBeneficiaryId(partnerId)
                    .toProjectPartnerSpfBudgetPerPeriod()
            },
            previousVersionFetcher = { timestamp ->
                projectPartnerRepository.getSpfBudgetByBeneficiaryIdAsOfTimestamp(partnerId, timestamp)
                    .toProjectPartnerSpfBudgetPerPeriod()
            }) ?: emptyList()

}
