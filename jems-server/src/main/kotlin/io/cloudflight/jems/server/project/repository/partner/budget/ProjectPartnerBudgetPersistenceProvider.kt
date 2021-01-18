package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeUnitCostRepository
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetPersistence
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProjectPartnerBudgetPersistenceProvider(
    private val budgetStaffCostRepository: ProjectPartnerBudgetStaffCostRepository,
    private val budgetTravelRepository: ProjectPartnerBudgetTravelRepository,
    private val budgetExternalRepository: ProjectPartnerBudgetExternalRepository,
    private val budgetEquipmentRepository: ProjectPartnerBudgetEquipmentRepository,
    private val budgetInfrastructureRepository: ProjectPartnerBudgetInfrastructureRepository,
    private val budgetUnitCostRepository: ProjectPartnerBudgetUnitCostRepository,
    private val programmeUnitCostRepository: ProgrammeUnitCostRepository
) : ProjectPartnerBudgetPersistence {

    @Transactional(readOnly = true)
    override fun getBudgetStaffCosts(partnerId: Long) =
        budgetStaffCostRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId).toBudgetStaffCostEntries()

    @Transactional
    override fun createOrUpdateBudgetStaffCosts(partnerId: Long, staffCosts: List<BudgetStaffCostEntry>) =
        budgetStaffCostRepository.saveAll(staffCosts.map { it.toProjectPartnerBudgetStaffCostEntity(partnerId) }).map { it.toBudgetStaffCostEntry() }

    @Transactional
    override fun deleteAllBudgetStaffCostsExceptFor(partnerId: Long, idsToKeep: List<Long>) =
        if (idsToKeep.isNotEmpty()) budgetStaffCostRepository.deleteAllByBasePropertiesPartnerIdAndIdNotIn(partnerId, idsToKeep) else Unit

    @Transactional(readOnly = true)
    override fun getBudgetStaffCostTotal(partnerId: Long): BigDecimal =
        budgetStaffCostRepository.sumTotalForPartner(partnerId) ?: BigDecimal.ZERO


    @Transactional(readOnly = true)
    override fun getBudgetTravelAndAccommodationCosts(partnerId: Long) =
        budgetTravelRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId).toBudgetTravelAndAccommodationCostEntries()

    @Transactional
    override fun createOrUpdateBudgetTravelAndAccommodationCosts(partnerId: Long, travelAndAccommodationCosts: List<BudgetTravelAndAccommodationCostEntry>) =
        budgetTravelRepository.saveAll(travelAndAccommodationCosts.map { it.toProjectPartnerBudgetTravelEntity(partnerId) }).map { it.toBudgetTravelAndAccommodationCostEntry() }

    @Transactional
    override fun deleteAllBudgetTravelAndAccommodationCostsExceptFor(partnerId: Long, idsToKeep: List<Long>) =
        if (idsToKeep.isNotEmpty()) budgetTravelRepository.deleteAllByBasePropertiesPartnerIdAndIdNotIn(partnerId, idsToKeep) else Unit

    @Transactional(readOnly = true)
    override fun getBudgetTravelAndAccommodationCostTotal(partnerId: Long): BigDecimal =
        budgetTravelRepository.sumTotalForPartner(partnerId) ?: BigDecimal.ZERO


    @Transactional(readOnly = true)
    override fun getBudgetInfrastructureAndWorksCosts(partnerId: Long) =
        budgetInfrastructureRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId).infrastructureEntitiesToBudgetGeneralEntries()

    @Transactional
    override fun createOrUpdateBudgetInfrastructureAndWorksCosts(partnerId: Long, infrastructureAndWorksCosts: List<BudgetGeneralCostEntry>) =
        budgetInfrastructureRepository.saveAll(infrastructureAndWorksCosts.map { it.toProjectPartnerBudgetInfrastructureEntity(partnerId) }).map { it.toBudgetGeneralCostEntry() }

    @Transactional
    override fun deleteAllBudgetInfrastructureAndWorksCostsExceptFor(partnerId: Long, idsToKeep: List<Long>) =
        if (idsToKeep.isNotEmpty()) budgetInfrastructureRepository.deleteAllByBasePropertiesPartnerIdAndIdNotIn(partnerId, idsToKeep) else Unit

    @Transactional(readOnly = true)
    override fun getBudgetUnitCosts(partnerId: Long): List<BudgetUnitCostEntry> =
        budgetUnitCostRepository.findAllByPartnerIdOrderByIdAsc(partnerId).toModel()

    @Transactional(readOnly = true)
    override fun getBudgetUnitCostTotal(partnerId: Long): BigDecimal =
        budgetUnitCostRepository.sumTotalForPartner(partnerId) ?: BigDecimal.ZERO

    @Transactional
    override fun deleteAllUnitCostsExceptFor(partnerId: Long, idsToKeep: Set<Long>) =
        if (idsToKeep.isNotEmpty()) budgetUnitCostRepository.deleteAllByPartnerIdAndIdNotIn(partnerId, idsToKeep) else Unit

    @Transactional
    override fun createOrUpdateBudgetUnitCosts(partnerId: Long, unitCosts: List<BudgetUnitCostEntry>): List<BudgetUnitCostEntry> =
        budgetUnitCostRepository.saveAll(unitCosts.toEntity(
            partnerId = partnerId,
            getProgrammeUnitCost = { getProgrammeUnitCostOrThrow(it) }
        )).toModel()

    private fun getProgrammeUnitCostOrThrow(programmeUnitCostId: Long) =
        programmeUnitCostRepository.findById(programmeUnitCostId).orElseThrow { ResourceNotFoundException("programmeUnitCost") }

    @Transactional(readOnly = true)
    override fun getBudgetInfrastructureAndWorksCostTotal(partnerId: Long): BigDecimal =
        budgetInfrastructureRepository.sumTotalForPartner(partnerId) ?: BigDecimal.ZERO


    @Transactional(readOnly = true)
    override fun getBudgetExternalExpertiseAndServicesCosts(partnerId: Long) =
        budgetExternalRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId).externalEntitiesToBudgetGeneralEntries()

    @Transactional
    override fun createOrUpdateBudgetExternalExpertiseAndServicesCosts(partnerId: Long, externalExpertiseAndServicesCosts: List<BudgetGeneralCostEntry>) =
        budgetExternalRepository.saveAll(externalExpertiseAndServicesCosts.map { it.toProjectPartnerBudgetExternalEntity(partnerId) }).map { it.toBudgetGeneralCostEntry() }

    @Transactional
    override fun deleteAllBudgetExternalExpertiseAndServicesCostsExceptFor(partnerId: Long, idsToKeep: List<Long>) =
        if (idsToKeep.isNotEmpty()) budgetExternalRepository.deleteAllByBasePropertiesPartnerIdAndIdNotIn(partnerId, idsToKeep) else Unit

    @Transactional(readOnly = true)
    override fun getBudgetExternalExpertiseAndServicesCostTotal(partnerId: Long): BigDecimal =
        budgetExternalRepository.sumTotalForPartner(partnerId) ?: BigDecimal.ZERO


    @Transactional(readOnly = true)
    override fun getBudgetEquipmentCosts(partnerId: Long) =
        budgetEquipmentRepository.findAllByBasePropertiesPartnerIdOrderByIdAsc(partnerId).equipmentEntitiesToBudgetGeneralEntries()

    @Transactional
    override fun createOrUpdateBudgetEquipmentCosts(partnerId: Long, equipmentCosts: List<BudgetGeneralCostEntry>) =
        budgetEquipmentRepository.saveAll(equipmentCosts.map { it.toProjectPartnerBudgetEquipmentEntity(partnerId) }).map { it.toBudgetGeneralCostEntry() }

    @Transactional
    override fun deleteAllBudgetEquipmentCostsExceptFor(partnerId: Long, idsToKeep: List<Long>) =
        if (idsToKeep.isNotEmpty()) budgetEquipmentRepository.deleteAllByBasePropertiesPartnerIdAndIdNotIn(partnerId, idsToKeep) else Unit

    @Transactional(readOnly = true)
    override fun getBudgetEquipmentCostTotal(partnerId: Long): BigDecimal =
        budgetEquipmentRepository.sumTotalForPartner(partnerId) ?: BigDecimal.ZERO

}
