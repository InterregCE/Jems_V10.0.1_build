package io.cloudflight.jems.server.project.repository.partner.budget.options

import io.cloudflight.jems.server.call.repository.toModel
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetEquipmentRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetExternalRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetInfrastructureRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetStaffCostRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetTravelRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetUnitCostRepository
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectPartnerBudgetOptionsPersistenceProvider(
    private val projectVersionUtils: ProjectVersionUtils,
    private val projectPersistence: ProjectPersistence,
    private val budgetOptionsRepository: ProjectPartnerBudgetOptionsRepository,
    private val budgetStaffCostRepository: ProjectPartnerBudgetStaffCostRepository,
    private val budgetTravelRepository: ProjectPartnerBudgetTravelRepository,
    private val budgetExternalRepository: ProjectPartnerBudgetExternalRepository,
    private val budgetEquipmentRepository: ProjectPartnerBudgetEquipmentRepository,
    private val budgetInfrastructureRepository: ProjectPartnerBudgetInfrastructureRepository,
    private val budgetUnitCostRepository: ProjectPartnerBudgetUnitCostRepository,
    private val partnerRepository: ProjectPartnerRepository,
) : ProjectPartnerBudgetOptionsPersistence {

    @Transactional(readOnly = true)
    override fun getBudgetOptions(partnerId: Long, version: String?): ProjectPartnerBudgetOptions? =
        projectVersionUtils.fetch(version, projectPersistence.getProjectIdForPartner(partnerId),
            currentVersionFetcher = {
                budgetOptionsRepository.findById(partnerId).map { it.toProjectPartnerBudgetOptions() }.orElse(null)
            },
            previousVersionFetcher = { timestamp ->
                budgetOptionsRepository.findByPartnerIdAsOfTimestamp(partnerId, timestamp)
                    .map { it.toProjectPartnerBudgetOptions() }.orElse(null)
            }
        )

    @Transactional(readOnly = true)
    override fun getBudgetOptions(partnerIds: Set<Long>): List<ProjectPartnerBudgetOptions> =
        budgetOptionsRepository.findAllById(partnerIds).toProjectPartnerBudgetOptions()

    @Transactional
    override fun updateBudgetOptions(partnerId: Long, options: ProjectPartnerBudgetOptions) {
        budgetOptionsRepository.save(options.toProjectPartnerBudgetOptionsEntity())
    }

    @Transactional
    override fun deleteBudgetOptions(partnerId: Long) {
        if (budgetOptionsRepository.existsById(partnerId))
            budgetOptionsRepository.deleteById(partnerId)
    }

    @Transactional
    override fun deleteStaffCosts(partnerId: Long) =
        budgetStaffCostRepository.deleteAllByBasePropertiesPartnerId(partnerId)

    @Transactional
    override fun deleteExternalCosts(partnerId: Long) =
        this.budgetExternalRepository.deleteAllByBasePropertiesPartnerId(partnerId)

    @Transactional
    override fun deleteEquipmentCosts(partnerId: Long) =
        this.budgetEquipmentRepository.deleteAllByBasePropertiesPartnerId(partnerId)

    @Transactional
    override fun deleteInfrastructureCosts(partnerId: Long) =
        this.budgetInfrastructureRepository.deleteAllByBasePropertiesPartnerId(partnerId)

    @Transactional
    override fun deleteTravelAndAccommodationCosts(partnerId: Long) =
        budgetTravelRepository.deleteAllByBasePropertiesPartnerId(partnerId)

    @Transactional
    override fun deleteUnitCosts(partnerId: Long) =
        budgetUnitCostRepository.deleteAllByBasePropertiesPartnerId(partnerId)

    @Transactional(readOnly = true)
    override fun getProjectCallFlatRateByPartnerId(partnerId: Long): Set<ProjectCallFlatRate> =
        partnerRepository.getOne(partnerId).project.call.flatRates.toModel()

}
