package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.general.ProjectPartnerBudgetGeneralRow
import io.cloudflight.jems.server.project.entity.partner.budget.staff_cost.ProjectPartnerBudgetStaffCostRow
import io.cloudflight.jems.server.project.entity.partner.budget.travel.ProjectPartnerBudgetTravelCostRow
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.budget.ProjectLumpSumRepository
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toBudgetGeneralCostEntryList
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toBudgetGeneralEntryList
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toBudgetStaffCostEntries
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toBudgetStaffCostEntryList
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toBudgetTravelAndAccommodationCostEntries
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toBudgetTravelCostEntryList
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toBudgetUnitCostEntryList
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toModel
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProjectPartnerBudgetCostsPersistenceProvider(
    private val projectVersionUtils: ProjectVersionUtils,
    private val projectPersistence: ProjectPersistence,
    private val budgetStaffCostRepository: ProjectPartnerBudgetStaffCostRepository,
    private val budgetTravelRepository: ProjectPartnerBudgetTravelRepository,
    private val budgetExternalRepository: ProjectPartnerBudgetExternalRepository,
    private val budgetEquipmentRepository: ProjectPartnerBudgetEquipmentRepository,
    private val budgetInfrastructureRepository: ProjectPartnerBudgetInfrastructureRepository,
    private val budgetUnitCostRepository: ProjectPartnerBudgetUnitCostRepository,
    private val budgetLumpSumRepository: ProjectLumpSumRepository
) : ProjectPartnerBudgetCostsPersistence {

    @Transactional(readOnly = true)
    override fun getBudgetStaffCosts(partnerId: Long, version: Int?): List<BudgetStaffCostEntry> =
        projectVersionUtils.fetch(version, projectPersistence.getProjectIdForPartner(partnerId),
            currentVersionFetcher = {
                budgetStaffCostRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId)
                    .toBudgetStaffCostEntries()
            },
            previousVersionFetcher = { timestamp ->
                budgetStaffCostRepository.findAllByPartnerIdAsOfTimestamp(
                    partnerId, timestamp, ProjectPartnerBudgetStaffCostRow::class.java
                ).toBudgetStaffCostEntryList()
            }
        )

    @Transactional(readOnly = true)
    override fun getBudgetStaffCostTotal(partnerId: Long, version: Int?): BigDecimal =
        budgetStaffCostRepository.sumTotalForPartner(partnerId) ?: BigDecimal.ZERO

    @Transactional(readOnly = true)
    override fun getBudgetTravelAndAccommodationCosts(partnerId: Long, version: Int?) =
        projectVersionUtils.fetch(version, projectPersistence.getProjectIdForPartner(partnerId),
            currentVersionFetcher = {
                budgetTravelRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId)
                    .toBudgetTravelAndAccommodationCostEntries()
            },
            previousVersionFetcher = { timestamp ->
                budgetTravelRepository.findAllByPartnerIdAsOfTimestamp(
                    partnerId, timestamp, ProjectPartnerBudgetTravelCostRow::class.java
                ).toBudgetTravelCostEntryList()
            }
        )


    @Transactional(readOnly = true)
    override fun getBudgetTravelAndAccommodationCostTotal(partnerId: Long, version: Int?): BigDecimal =
        budgetTravelRepository.sumTotalForPartner(partnerId) ?: BigDecimal.ZERO

    @Transactional(readOnly = true)
    override fun getBudgetInfrastructureAndWorksCosts(partnerId: Long, version: Int?) =
        projectVersionUtils.fetch(version, projectPersistence.getProjectIdForPartner(partnerId),
            currentVersionFetcher = {
                budgetInfrastructureRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId)
                    .toBudgetGeneralEntryList()
            },
            previousVersionFetcher = { timestamp ->
                budgetInfrastructureRepository.findAllByPartnerIdAsOfTimestamp(
                    partnerId, timestamp, ProjectPartnerBudgetGeneralRow::class.java
                ).toBudgetGeneralCostEntryList()
            }
        )

    @Transactional(readOnly = true)
    override fun getBudgetUnitCosts(partnerId: Long, version: Int?): List<BudgetUnitCostEntry> =
        projectVersionUtils.fetch(version, projectPersistence.getProjectIdForPartner(partnerId),
            currentVersionFetcher = {
                budgetUnitCostRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId).toModel()
            },
            previousVersionFetcher = { timestamp ->
                budgetUnitCostRepository.findAllByPartnerIdAsOfTimestamp(partnerId, timestamp)
                    .toBudgetUnitCostEntryList()
            }
        )

    @Transactional(readOnly = true)
    override fun getBudgetUnitCostTotal(partnerId: Long, version: Int?): BigDecimal =
        budgetUnitCostRepository.sumTotalForPartner(partnerId) ?: BigDecimal.ZERO

    @Transactional(readOnly = true)
    override fun getBudgetInfrastructureAndWorksCostTotal(partnerId: Long, version: Int?): BigDecimal =
        budgetInfrastructureRepository.sumTotalForPartner(partnerId) ?: BigDecimal.ZERO

    @Transactional(readOnly = true)
    override fun getBudgetExternalExpertiseAndServicesCosts(partnerId: Long, version: Int?) =
        projectVersionUtils.fetch(version, projectPersistence.getProjectIdForPartner(partnerId),
            currentVersionFetcher = {
                budgetExternalRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId)
                    .toBudgetGeneralEntryList()
            },
            previousVersionFetcher = { timestamp ->
                budgetExternalRepository.findAllByPartnerIdAsOfTimestamp(
                    partnerId, timestamp, ProjectPartnerBudgetGeneralRow::class.java
                ).toBudgetGeneralCostEntryList()
            }
        )


    @Transactional(readOnly = true)
    override fun getBudgetExternalExpertiseAndServicesCostTotal(partnerId: Long, version: Int?): BigDecimal =
        budgetExternalRepository.sumTotalForPartner(partnerId) ?: BigDecimal.ZERO

    @Transactional(readOnly = true)
    override fun getBudgetEquipmentCosts(partnerId: Long, version: Int?) =
        projectVersionUtils.fetch(version, projectPersistence.getProjectIdForPartner(partnerId),
            currentVersionFetcher = {
                budgetEquipmentRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId)
                    .toBudgetGeneralEntryList()
            },
            previousVersionFetcher = { timestamp ->
                budgetExternalRepository.findAllByPartnerIdAsOfTimestamp(
                    partnerId, timestamp, ProjectPartnerBudgetGeneralRow::class.java
                ).toBudgetGeneralCostEntryList()
            }
        )


    @Transactional(readOnly = true)
    override fun getBudgetEquipmentCostTotal(partnerId: Long, version: Int?): BigDecimal =
        budgetEquipmentRepository.sumTotalForPartner(partnerId) ?: BigDecimal.ZERO

    @Transactional(readOnly = true)
    override fun getBudgetLumpSumsCostTotal(partnerId: Long): BigDecimal =
        budgetLumpSumRepository.getPartnerLumpSumsTotal(partnerId) ?: BigDecimal.ZERO

}
