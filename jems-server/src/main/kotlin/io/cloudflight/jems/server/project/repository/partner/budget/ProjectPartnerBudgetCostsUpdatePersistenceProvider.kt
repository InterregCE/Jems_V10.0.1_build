package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeUnitCostRepository
import io.cloudflight.jems.server.project.entity.ProjectPeriodId
import io.cloudflight.jems.server.project.repository.description.ProjectPeriodRepository
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toBudgetGeneralCostEntry
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toBudgetSpfCostEntry
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toBudgetStaffCostEntry
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toBudgetTravelAndAccommodationCostEntry
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toBudgetUnitCostEntities
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toModel
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toProjectPartnerBudgetEquipmentEntity
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toProjectPartnerBudgetExternalEntities
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toProjectPartnerBudgetInfrastructureEntity
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toProjectPartnerBudgetSpfCostEntities
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toProjectPartnerBudgetStaffCostEntities
import io.cloudflight.jems.server.project.repository.partner.budget.mappers.toProjectPartnerBudgetTravelEntities
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsUpdatePersistence
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetSpfCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectPartnerBudgetCostsUpdatePersistenceProvider(
    private val budgetStaffCostRepository: ProjectPartnerBudgetStaffCostRepository,
    private val budgetTravelRepository: ProjectPartnerBudgetTravelRepository,
    private val budgetExternalRepository: ProjectPartnerBudgetExternalRepository,
    private val budgetEquipmentRepository: ProjectPartnerBudgetEquipmentRepository,
    private val budgetInfrastructureRepository: ProjectPartnerBudgetInfrastructureRepository,
    private val budgetUnitCostRepository: ProjectPartnerBudgetUnitCostRepository,
    private val budgetSpfCostRepository: ProjectPartnerBudgetSpfCostRepository,
    private val programmeUnitCostRepository: ProgrammeUnitCostRepository,
    private val projectPeriodRepository: ProjectPeriodRepository,
) : ProjectPartnerBudgetCostsUpdatePersistence {

    @Transactional
    override fun createOrUpdateBudgetStaffCosts(
        projectId: Long,
        partnerId: Long,
        staffCosts: List<BudgetStaffCostEntry>
    ) =
        budgetStaffCostRepository.saveAll(
            staffCosts.toProjectPartnerBudgetStaffCostEntities(
                partnerId,
                projectPeriodEntityResolver = projectPeriodEntityReferenceResolver(projectId)
            )
        ).map { it.toBudgetStaffCostEntry() }

    @Transactional
    override fun deleteAllBudgetStaffCostsExceptFor(partnerId: Long, idsToKeep: Set<Long>) =
        if (idsToKeep.isNotEmpty()) budgetStaffCostRepository.deleteAllByBasePropertiesPartnerIdAndIdNotIn(
            partnerId,
            idsToKeep
        ) else budgetStaffCostRepository.deleteAllByBasePropertiesPartnerId(partnerId)

    @Transactional
    override fun createOrUpdateBudgetTravelAndAccommodationCosts(
        projectId: Long,
        partnerId: Long,
        travelAndAccommodationCosts: List<BudgetTravelAndAccommodationCostEntry>
    ) =
        budgetTravelRepository.saveAll(
            travelAndAccommodationCosts.toProjectPartnerBudgetTravelEntities(
                partnerId = partnerId,
                projectPeriodEntityReferenceResolver = projectPeriodEntityReferenceResolver(projectId)
            )
        ).map { it.toBudgetTravelAndAccommodationCostEntry() }

    @Transactional
    override fun deleteAllBudgetTravelAndAccommodationCostsExceptFor(partnerId: Long, idsToKeep: Set<Long>) =
        if (idsToKeep.isNotEmpty()) budgetTravelRepository.deleteAllByBasePropertiesPartnerIdAndIdNotIn(
            partnerId,
            idsToKeep
        ) else budgetTravelRepository.deleteAllByBasePropertiesPartnerId(partnerId)

    @Transactional
    override fun createOrUpdateBudgetInfrastructureAndWorksCosts(
        projectId: Long,
        partnerId: Long,
        infrastructureAndWorksCosts: List<BudgetGeneralCostEntry>
    ) =
        budgetInfrastructureRepository.saveAll(
            infrastructureAndWorksCosts.toProjectPartnerBudgetInfrastructureEntity(
                partnerId = partnerId,
                projectPeriodEntityReferenceResolver = projectPeriodEntityReferenceResolver(projectId)
            )
        ).map { it.toBudgetGeneralCostEntry() }

    @Transactional
    override fun deleteAllBudgetInfrastructureAndWorksCostsExceptFor(partnerId: Long, idsToKeep: Set<Long>) =
        if (idsToKeep.isNotEmpty()) budgetInfrastructureRepository.deleteAllByBasePropertiesPartnerIdAndIdNotIn(
            partnerId,
            idsToKeep
        ) else budgetInfrastructureRepository.deleteAllByBasePropertiesPartnerId(partnerId)

    @Transactional
    override fun deleteAllUnitCostsExceptFor(partnerId: Long, idsToKeep: Set<Long>) =
        if (idsToKeep.isNotEmpty()) budgetUnitCostRepository.deleteAllByBasePropertiesPartnerIdAndIdNotIn(
            partnerId,
            idsToKeep
        ) else budgetUnitCostRepository.deleteAllByBasePropertiesPartnerId(partnerId)

    @Transactional
    override fun createOrUpdateBudgetUnitCosts(
        projectId: Long,
        partnerId: Long,
        unitCosts: List<BudgetUnitCostEntry>
    ): List<BudgetUnitCostEntry> =
        budgetUnitCostRepository.saveAll(unitCosts.toBudgetUnitCostEntities(
            partnerId = partnerId,
            projectPeriodEntityReferenceResolver = projectPeriodEntityReferenceResolver(projectId),
            getProgrammeUnitCost = { programmeUnitCostRepository.getReferenceById(it) }
        )).toModel()


    @Transactional
    override fun createOrUpdateBudgetExternalExpertiseAndServicesCosts(
        projectId: Long,
        partnerId: Long,
        externalExpertiseAndServicesCosts: List<BudgetGeneralCostEntry>
    ) =
        budgetExternalRepository.saveAll(
            externalExpertiseAndServicesCosts.toProjectPartnerBudgetExternalEntities(
                partnerId = partnerId,
                projectPeriodEntityReferenceResolver = projectPeriodEntityReferenceResolver(projectId)
            )
        ).map { it.toBudgetGeneralCostEntry() }

    @Transactional
    override fun deleteAllBudgetExternalExpertiseAndServicesCostsExceptFor(partnerId: Long, idsToKeep: Set<Long>) =
        if (idsToKeep.isNotEmpty()) budgetExternalRepository.deleteAllByBasePropertiesPartnerIdAndIdNotIn(
            partnerId,
            idsToKeep
        ) else budgetExternalRepository.deleteAllByBasePropertiesPartnerId(partnerId)

    @Transactional
    override fun createOrUpdateBudgetEquipmentCosts(
        projectId: Long,
        partnerId: Long,
        equipmentCosts: List<BudgetGeneralCostEntry>
    ) =
        budgetEquipmentRepository.saveAll(
            equipmentCosts.toProjectPartnerBudgetEquipmentEntity(
                partnerId = partnerId,
                projectPeriodEntityReferenceResolver = projectPeriodEntityReferenceResolver(projectId)
            )
        ).map { it.toBudgetGeneralCostEntry() }

    @Transactional
    override fun deleteAllBudgetEquipmentCostsExceptFor(partnerId: Long, idsToKeep: Set<Long>) =
        if (idsToKeep.isNotEmpty()) budgetEquipmentRepository.deleteAllByBasePropertiesPartnerIdAndIdNotIn(
            partnerId,
            idsToKeep
        ) else budgetEquipmentRepository.deleteAllByBasePropertiesPartnerId(partnerId)

    fun projectPeriodEntityReferenceResolver(projectId: Long) =
        { periodNumber: Int ->
            projectPeriodRepository.getReferenceById(
                ProjectPeriodId(
                    projectId,
                    periodNumber
                )
            )
        }

    @Transactional
    override fun createOrUpdateBudgetSpfCosts(
        projectId: Long,
        partnerId: Long,
        spfCosts: List<BudgetSpfCostEntry>
    ) =
        budgetSpfCostRepository.saveAll(
            spfCosts.toProjectPartnerBudgetSpfCostEntities(
                partnerId = partnerId,
                projectPeriodEntityReferenceResolver = projectPeriodEntityReferenceResolver(projectId)
            )
        ).map { it.toBudgetSpfCostEntry() }

    @Transactional
    override fun deleteAllBudgetSpfCostsExceptFor(partnerId: Long, idsToKeep: Set<Long>) =
        if (idsToKeep.isNotEmpty()) budgetSpfCostRepository.deleteAllByBasePropertiesPartnerIdAndIdNotIn(
            partnerId,
            idsToKeep
        ) else budgetSpfCostRepository.deleteAllByBasePropertiesPartnerId(partnerId)
}
