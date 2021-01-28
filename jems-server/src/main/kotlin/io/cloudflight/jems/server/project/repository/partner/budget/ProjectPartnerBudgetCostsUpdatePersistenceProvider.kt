package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeUnitCostRepository
import io.cloudflight.jems.server.project.entity.ProjectPeriodId
import io.cloudflight.jems.server.project.repository.description.ProjectPeriodRepository
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsUpdatePersistence
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
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
    private val programmeUnitCostRepository: ProgrammeUnitCostRepository,
    private val projectPeriodRepository: ProjectPeriodRepository,
) : ProjectPartnerBudgetCostsUpdatePersistence {

    @Transactional
    override fun createOrUpdateBudgetStaffCosts(
        projectId: Long,
        partnerId: Long,
        staffCosts: Set<BudgetStaffCostEntry>
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
        travelAndAccommodationCosts: Set<BudgetTravelAndAccommodationCostEntry>
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
        infrastructureAndWorksCosts: Set<BudgetGeneralCostEntry>
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
        unitCosts: Set<BudgetUnitCostEntry>
    ): List<BudgetUnitCostEntry> =
        budgetUnitCostRepository.saveAll(unitCosts.toBudgetUnitCostEntities(
            partnerId = partnerId,
            projectPeriodEntityReferenceResolver = projectPeriodEntityReferenceResolver(projectId),
            getProgrammeUnitCost = { programmeUnitCostRepository.getOne(it) }
        )).toModel()


    @Transactional
    override fun createOrUpdateBudgetExternalExpertiseAndServicesCosts(
        projectId: Long,
        partnerId: Long,
        externalExpertiseAndServicesCosts: Set<BudgetGeneralCostEntry>
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
        equipmentCosts: Set<BudgetGeneralCostEntry>
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
            projectPeriodRepository.getOne(
                ProjectPeriodId(
                    projectId,
                    periodNumber
                )
            )
        }

}
