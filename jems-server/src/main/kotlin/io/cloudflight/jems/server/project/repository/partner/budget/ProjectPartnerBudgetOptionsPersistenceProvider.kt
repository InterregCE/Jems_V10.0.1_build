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
) : ProjectPartnerBudgetOptionsPersistence {

    @Transactional(readOnly = true)
    override fun getBudgetOptions(partnerId: Long): ProjectPartnerBudgetOptions? =
        budgetOptionsRepository.findById(partnerId).map { it.toModel() }.orElse(null)

    @Transactional(readOnly = true)
    override fun getBudgetOptions(partnerIds: Set<Long>): List<ProjectPartnerBudgetOptions> =
        budgetOptionsRepository.findAllById(partnerIds).toModel()

    @Transactional
    override fun updateBudgetOptions(partnerId: Long, options: ProjectPartnerBudgetOptions) {
        budgetOptionsRepository.findById(partnerId).ifPresentOrElse(
            {
                it.officeAdministrationFlatRate = options.officeAndAdministrationFlatRate
                it.travelAccommodationFlatRate = options.travelAndAccommodationFlatRate
                it.staffCostsFlatRate = options.staffCostsFlatRate
            },
            {
                budgetOptionsRepository.save(options.toEntity())
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
    override fun deleteTravelAndAccommodationCosts(partnerId: Long) =
        travelRepository.deleteAllByPartnerId(partnerId)

}
