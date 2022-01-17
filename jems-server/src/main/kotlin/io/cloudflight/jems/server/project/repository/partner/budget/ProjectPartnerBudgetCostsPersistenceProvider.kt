package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetBase
import io.cloudflight.jems.server.project.entity.partner.budget.general.ProjectPartnerBudgetGeneralRow
import io.cloudflight.jems.server.project.entity.partner.budget.staff_cost.ProjectPartnerBudgetStaffCostRow
import io.cloudflight.jems.server.project.entity.partner.budget.travel.ProjectPartnerBudgetTravelCostRow
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.budget.ProjectPartnerLumpSumRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toBudgetGeneralCostEntryList
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toBudgetGeneralEntryList
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toBudgetStaffCostEntries
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toBudgetStaffCostEntryList
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toBudgetTravelAndAccommodationCostEntries
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toBudgetTravelCostEntryList
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toBudgetUnitCostEntryList
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toModel
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProjectPartnerBudgetCostsPersistenceProvider(
    private val projectVersionUtils: ProjectVersionUtils,
    private val projectPartnerRepository: ProjectPartnerRepository,
    private val budgetStaffCostRepository: ProjectPartnerBudgetStaffCostRepository,
    private val budgetTravelRepository: ProjectPartnerBudgetTravelRepository,
    private val budgetExternalRepository: ProjectPartnerBudgetExternalRepository,
    private val budgetEquipmentRepository: ProjectPartnerBudgetEquipmentRepository,
    private val budgetInfrastructureRepository: ProjectPartnerBudgetInfrastructureRepository,
    private val budgetUnitCostRepository: ProjectPartnerBudgetUnitCostRepository,
    private val budgetPartnerLumpSumRepository: ProjectPartnerLumpSumRepository
) : ProjectPartnerBudgetCostsPersistence {

    @Transactional(readOnly = true)
    override fun getBudgetStaffCosts(partnerId: Long, version: String?): List<BudgetStaffCostEntry> =
        projectVersionUtils.fetch(version, getProjectIdForPartner(partnerId, version),
            currentVersionFetcher = {
                budgetStaffCostRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId)
                    .toBudgetStaffCostEntries()
            },
            previousVersionFetcher = { timestamp ->
                budgetStaffCostRepository.findAllByPartnerIdAsOfTimestamp(
                    partnerId, timestamp, ProjectPartnerBudgetStaffCostRow::class.java
                ).toBudgetStaffCostEntryList()
            }
        ) ?: emptyList()

    @Transactional(readOnly = true)
    override fun getBudgetTravelAndAccommodationCosts(partnerId: Long, version: String?) =
        projectVersionUtils.fetch(version, getProjectIdForPartner(partnerId, version),
            currentVersionFetcher = {
                budgetTravelRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId)
                    .toBudgetTravelAndAccommodationCostEntries()
            },
            previousVersionFetcher = { timestamp ->
                budgetTravelRepository.findAllByPartnerIdAsOfTimestamp(
                    partnerId, timestamp, ProjectPartnerBudgetTravelCostRow::class.java
                ).toBudgetTravelCostEntryList()
            }
        ) ?: emptyList()

    @Transactional(readOnly = true)
    override fun getBudgetInfrastructureAndWorksCosts(partnerId: Long, version: String?) =
        projectVersionUtils.fetch(version, getProjectIdForPartner(partnerId, version),
            currentVersionFetcher = {
                budgetInfrastructureRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId)
                    .toBudgetGeneralEntryList()
            },
            previousVersionFetcher = { timestamp ->
                budgetInfrastructureRepository.findAllByPartnerIdAsOfTimestamp(
                    partnerId, timestamp, ProjectPartnerBudgetGeneralRow::class.java
                ).toBudgetGeneralCostEntryList()
            }
        ) ?: emptyList()

    @Transactional(readOnly = true)
    override fun getBudgetUnitCosts(partnerId: Long, version: String?): List<BudgetUnitCostEntry> =
        projectVersionUtils.fetch(version, getProjectIdForPartner(partnerId, version),
            currentVersionFetcher = {
                budgetUnitCostRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId).toModel()
            },
            previousVersionFetcher = { timestamp ->
                budgetUnitCostRepository.findAllByPartnerIdAsOfTimestamp(partnerId, timestamp)
                    .toBudgetUnitCostEntryList()
            }
        ) ?: emptyList()

    @Transactional(readOnly = true)
    override fun getBudgetExternalExpertiseAndServicesCosts(partnerId: Long, version: String?) =
        projectVersionUtils.fetch(version, getProjectIdForPartner(partnerId, version),
            currentVersionFetcher = {
                budgetExternalRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId)
                    .toBudgetGeneralEntryList()
            },
            previousVersionFetcher = { timestamp ->
                budgetExternalRepository.findAllByPartnerIdAsOfTimestamp(
                    partnerId, timestamp, ProjectPartnerBudgetGeneralRow::class.java
                ).toBudgetGeneralCostEntryList()
            }
        ) ?: emptyList()

    @Transactional(readOnly = true)
    override fun getBudgetEquipmentCosts(partnerId: Long, version: String?) =
        projectVersionUtils.fetch(version, getProjectIdForPartner(partnerId, version),
            currentVersionFetcher = {
                budgetEquipmentRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId)
                    .toBudgetGeneralEntryList()
            },
            previousVersionFetcher = { timestamp ->
                budgetEquipmentRepository.findAllByPartnerIdAsOfTimestamp(
                    partnerId, timestamp, ProjectPartnerBudgetGeneralRow::class.java
                ).toBudgetGeneralCostEntryList()
            }
        ) ?: emptyList()

    @Transactional(readOnly = true)
    override fun getBudgetStaffCostTotal(partnerId: Long, version: String?): BigDecimal =
        getCostTotal(partnerId, version, budgetStaffCostRepository)

    @Transactional(readOnly = true)
    override fun getBudgetTravelAndAccommodationCostTotal(partnerId: Long, version: String?): BigDecimal =
        getCostTotal(partnerId, version, budgetTravelRepository)

    @Transactional(readOnly = true)
    override fun getBudgetEquipmentCostTotal(partnerId: Long, version: String?): BigDecimal =
        getCostTotal(partnerId, version, budgetEquipmentRepository)

    @Transactional(readOnly = true)
    override fun getBudgetExternalExpertiseAndServicesCostTotal(partnerId: Long, version: String?): BigDecimal =
        getCostTotal(partnerId, version, budgetExternalRepository)

    @Transactional(readOnly = true)
    override fun getBudgetInfrastructureAndWorksCostTotal(partnerId: Long, version: String?): BigDecimal =
        getCostTotal(partnerId, version, budgetInfrastructureRepository)

    @Transactional(readOnly = true)
    override fun getBudgetUnitCostTotal(partnerId: Long, version: String?): BigDecimal =
        getCostTotal(partnerId, version, budgetUnitCostRepository)

    @Transactional(readOnly = true)
    override fun getBudgetLumpSumsCostTotal(partnerId: Long, version: String?): BigDecimal =
        projectVersionUtils.fetch(version, getProjectIdForPartner(partnerId, version),
            currentVersionFetcher = {
                budgetPartnerLumpSumRepository.sumTotalForPartner(partnerId)
            },
            previousVersionFetcher = { timestamp ->
                budgetPartnerLumpSumRepository.sumTotalForPartnerAsOfTimestamp(partnerId, timestamp)
            }
        ) ?: BigDecimal.ZERO

    private fun <T : ProjectPartnerBudgetBase> getCostTotal(
        partnerId: Long, version: String?, repository: ProjectPartnerBaseBudgetRepository<T>,
    ) =
        projectVersionUtils.fetch(version, getProjectIdForPartner(partnerId, version),
            currentVersionFetcher = {
                repository.sumTotalForPartner(partnerId)
            },
            previousVersionFetcher = { timestamp ->
                repository.sumTotalForPartnerAsOfTimestamp(partnerId, timestamp)
            }
        ) ?: BigDecimal.ZERO

    private fun getProjectIdForPartner(partnerId: Long, version: String?): Long {
        return projectVersionUtils.fetchProjectId(version, partnerId,
            currentVersionOnlyFetcher = { projectPartnerRepository.getProjectIdForPartner(partnerId) },
            historicVersionFetcher = { projectPartnerRepository.getProjectIdByPartnerIdInFullHistory(partnerId) }
        )
    }
}
