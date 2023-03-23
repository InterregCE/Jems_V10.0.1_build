package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetBase
import io.cloudflight.jems.server.project.entity.partner.budget.general.ProjectPartnerBudgetGeneralRow
import io.cloudflight.jems.server.project.entity.partner.budget.spf.ProjectPartnerBudgetSpfCostRow
import io.cloudflight.jems.server.project.entity.partner.budget.staff_cost.ProjectPartnerBudgetStaffCostRow
import io.cloudflight.jems.server.project.entity.partner.budget.travel.ProjectPartnerBudgetTravelCostRow
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.budget.ProjectPartnerLumpSumRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toBudgetGeneralCostEntryList
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toBudgetGeneralEntryList
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toBudgetSpfCostEntries
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toBudgetSpfCostEntryList
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
    private val budgetSpfCostRepository: ProjectPartnerBudgetSpfCostRepository,
    private val budgetPartnerLumpSumRepository: ProjectPartnerLumpSumRepository
) : ProjectPartnerBudgetCostsPersistence {

    @Transactional(readOnly = true)
    override fun getBudgetStaffCosts(partnerIds: Set<Long>, version: String?): List<BudgetStaffCostEntry> =
        projectVersionUtils.fetch(version, getProjectIdForPartner(partnerIds.first(), version),
            currentVersionFetcher = {
                budgetStaffCostRepository.findAllByBasePropertiesPartnerIdInOrderByIdAsc(partnerIds)
                    .toBudgetStaffCostEntries()
            },
            previousVersionFetcher = { timestamp ->
                budgetStaffCostRepository.findAllByPartnerIdAsOfTimestamp(
                    partnerIds, timestamp, ProjectPartnerBudgetStaffCostRow::class.java
                ).toBudgetStaffCostEntryList()
            }
        ) ?: emptyList()

    @Transactional(readOnly = true)
    override fun getBudgetTravelAndAccommodationCosts(partnerIds: Set<Long>, version: String?) =
        projectVersionUtils.fetch(version, getProjectIdForPartner(partnerIds.first(), version),
            currentVersionFetcher = {
                budgetTravelRepository.findAllByBasePropertiesPartnerIdInOrderByIdAsc(partnerIds)
                    .toBudgetTravelAndAccommodationCostEntries()
            },
            previousVersionFetcher = { timestamp ->
                budgetTravelRepository.findAllByPartnerIdAsOfTimestamp(
                    partnerIds, timestamp, ProjectPartnerBudgetTravelCostRow::class.java
                ).toBudgetTravelCostEntryList()
            }
        ) ?: emptyList()

    @Transactional(readOnly = true)
    override fun getBudgetInfrastructureAndWorksCosts(partnerIds: Set<Long>, version: String?) =
        projectVersionUtils.fetch(version, getProjectIdForPartner(partnerIds.first(), version),
            currentVersionFetcher = {
                budgetInfrastructureRepository.findAllByBasePropertiesPartnerIdInOrderByIdAsc(partnerIds)
                    .toBudgetGeneralEntryList()
            },
            previousVersionFetcher = { timestamp ->
                budgetInfrastructureRepository.findAllByPartnerIdAsOfTimestamp(
                    partnerIds, timestamp, ProjectPartnerBudgetGeneralRow::class.java
                ).toBudgetGeneralCostEntryList()
            }
        ) ?: emptyList()

    @Transactional(readOnly = true)
    override fun getBudgetUnitCosts(partnerIds: Set<Long>, version: String?): List<BudgetUnitCostEntry> =
        projectVersionUtils.fetch(version, getProjectIdForPartner(partnerIds.first(), version),
            currentVersionFetcher = {
                budgetUnitCostRepository.findAllByBasePropertiesPartnerIdInOrderByIdAsc(partnerIds).toModel()
            },
            previousVersionFetcher = { timestamp ->
                budgetUnitCostRepository.findAllByPartnerIdAsOfTimestamp(partnerIds, timestamp)
                    .toBudgetUnitCostEntryList()
            }
        ) ?: emptyList()

    @Transactional(readOnly = true)
    override fun getBudgetExternalExpertiseAndServicesCosts(partnerIds: Set<Long>, version: String?) =
        projectVersionUtils.fetch(version, getProjectIdForPartner(partnerIds.first(), version),
            currentVersionFetcher = {
                budgetExternalRepository.findAllByBasePropertiesPartnerIdInOrderByIdAsc(partnerIds)
                    .toBudgetGeneralEntryList()
            },
            previousVersionFetcher = { timestamp ->
                budgetExternalRepository.findAllByPartnerIdAsOfTimestamp(
                    partnerIds, timestamp, ProjectPartnerBudgetGeneralRow::class.java
                ).toBudgetGeneralCostEntryList()
            }
        ) ?: emptyList()

    @Transactional(readOnly = true)
    override fun getBudgetEquipmentCosts(partnerIds: Set<Long>, version: String?) =
        projectVersionUtils.fetch(version, getProjectIdForPartner(partnerIds.first(), version),
            currentVersionFetcher = {
                budgetEquipmentRepository.findAllByBasePropertiesPartnerIdInOrderByIdAsc(partnerIds)
                    .toBudgetGeneralEntryList()
            },
            previousVersionFetcher = { timestamp ->
                budgetEquipmentRepository.findAllByPartnerIdAsOfTimestamp(
                    partnerIds, timestamp, ProjectPartnerBudgetGeneralRow::class.java
                ).toBudgetGeneralCostEntryList()
            }
        ) ?: emptyList()

    @Transactional(readOnly = true)
    override fun getBudgetSpfCosts(partnerIds: Set<Long>, version: String?) =
        projectVersionUtils.fetch(version, getProjectIdForPartner(partnerIds.first(), version),
            currentVersionFetcher = {
                budgetSpfCostRepository.findAllByBasePropertiesPartnerIdInOrderByIdAsc(partnerIds)
                    .toBudgetSpfCostEntries()
            },
            previousVersionFetcher = { timestamp ->
                budgetSpfCostRepository.findAllByPartnerIdAsOfTimestamp(
                    partnerIds, timestamp, ProjectPartnerBudgetSpfCostRow::class.java
                ).toBudgetSpfCostEntryList()
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
    override fun getBudgetSpfCostTotal(partnerId: Long, version: String?): BigDecimal =
        getCostTotal(partnerId, version, budgetSpfCostRepository)

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

    @Transactional(readOnly = true)
    override fun isUnitCostUsed(unitCostId: Long): Boolean =
        budgetStaffCostRepository.existsByUnitCostId(unitCostId)
            || budgetExternalRepository.existsByUnitCostId(unitCostId)
            || budgetEquipmentRepository.existsByUnitCostId(unitCostId)
            || budgetInfrastructureRepository.existsByUnitCostId(unitCostId)
            || budgetTravelRepository.existsByUnitCostId(unitCostId)
            || budgetUnitCostRepository.existsByUnitCostId(unitCostId)

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
