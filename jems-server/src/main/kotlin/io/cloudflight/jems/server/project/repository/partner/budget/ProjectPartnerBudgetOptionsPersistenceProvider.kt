package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectPartnerBudgetOptionsPersistenceProvider(
    private val budgetOptionsRepository: ProjectPartnerBudgetOptionsRepository,
    private val budgetStaffCostRepository: ProjectPartnerBudgetStaffCostRepository,
    private val budgetTravelRepository: ProjectPartnerBudgetTravelRepository,
    private val budgetExternalRepository: ProjectPartnerBudgetExternalRepository,
    private val budgetEquipmentRepository: ProjectPartnerBudgetEquipmentRepository,
    private val budgetInfrastructureRepository: ProjectPartnerBudgetInfrastructureRepository,
    private val budgetUnitCostRepository: ProjectPartnerBudgetUnitCostRepository,
) : ProjectPartnerBudgetOptionsPersistence {

    @Transactional(readOnly = true)
    override fun getBudgetOptions(partnerId: Long): ProjectPartnerBudgetOptions? =
        budgetOptionsRepository.findById(partnerId).map { it.toProjectPartnerBudgetOptions() }.orElse(null)

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

}
