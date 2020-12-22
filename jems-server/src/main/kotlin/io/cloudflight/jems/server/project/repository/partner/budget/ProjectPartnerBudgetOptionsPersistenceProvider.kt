package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectPartnerBudgetOptionsPersistenceProvider(
    private val budgetOptionsRepository: ProjectPartnerBudgetOptionsRepository,
    private val staffCostRepository: ProjectPartnerBudgetStaffCostRepository,
    private val travelRepository: ProjectPartnerBudgetTravelRepository,
    private val externalRepository: ProjectPartnerBudgetExternalRepository,
    private val equipmentRepository: ProjectPartnerBudgetEquipmentRepository,
    private val infrastructureRepository: ProjectPartnerBudgetInfrastructureRepository,
) : ProjectPartnerBudgetOptionsPersistence {

    @Transactional(readOnly = true)
    override fun getBudgetOptions(partnerId: Long): ProjectPartnerBudgetOptions? =
        budgetOptionsRepository.findById(partnerId).map { it.toProjectPartnerBudgetOptions() }.orElse(null)

    @Transactional(readOnly = true)
    override fun getBudgetOptions(partnerIds: Set<Long>): List<ProjectPartnerBudgetOptions> =
        budgetOptionsRepository.findAllById(partnerIds).toProjectPartnerBudgetOptions()

    @Transactional
    override fun updateBudgetOptions(partnerId: Long, options: ProjectPartnerBudgetOptions) {
        budgetOptionsRepository.findById(partnerId).ifPresentOrElse(
            {
                it.officeAndAdministrationOnStaffCostsFlatRate = options.officeAndAdministrationOnStaffCostsFlatRate
                it.travelAndAccommodationOnStaffCostsFlatRate = options.travelAndAccommodationOnStaffCostsFlatRate
                it.staffCostsFlatRate = options.staffCostsFlatRate
                it.otherCostsOnStaffCostsFlatRate = options.otherCostsOnStaffCostsFlatRate
            },
            {
                budgetOptionsRepository.save(options.toProjectPartnerBudgetOptionsEntity())
            }
        )
    }

    @Transactional
    override fun deleteBudgetOptions(partnerId: Long) =
        budgetOptionsRepository.findById(partnerId).ifPresent {
            budgetOptionsRepository.deleteById(partnerId)
        }

    @Transactional
    override fun deleteStaffCosts(partnerId: Long) =
        staffCostRepository.deleteAllByPartnerId(partnerId)

    @Transactional
    override fun deleteExternalCosts(partnerId: Long) =
        this.externalRepository.deleteAllByPartnerId(partnerId)

    @Transactional
    override fun deleteEquipmentCosts(partnerId: Long) =
        this.equipmentRepository.deleteAllByPartnerId(partnerId)

    @Transactional
    override fun deleteInfrastructureCosts(partnerId: Long) =
        this.infrastructureRepository.deleteAllByPartnerId(partnerId)

    @Transactional
    override fun deleteTravelAndAccommodationCosts(partnerId: Long) =
        travelRepository.deleteAllByPartnerId(partnerId)

}
