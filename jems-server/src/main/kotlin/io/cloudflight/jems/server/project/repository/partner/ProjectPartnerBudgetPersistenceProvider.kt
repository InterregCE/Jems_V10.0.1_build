package io.cloudflight.jems.server.project.repository.partner

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetOptionsEntity
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetOptionsRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetStaffCostRepository
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectPartnerBudgetPersistenceProvider(
    private val budgetOptionsRepository: ProjectPartnerBudgetOptionsRepository,
    private val staffCostRepository: ProjectPartnerBudgetStaffCostRepository
) : ProjectPartnerBudgetPersistence {

    @Transactional(readOnly = true)
    override fun getBudgetOptions(partnerId: Long): ProjectPartnerBudgetOptions? =
        budgetOptionsRepository.findById(partnerId).map { it.toProjectPartnerBudgetOptions() }.orElse(null)

    @Transactional(readOnly = true)
    override fun getBudgetOptions(partnerIds: Set<Long>): List<ProjectPartnerBudgetOptions> =
        budgetOptionsRepository.findAllById(partnerIds).toProjectPartnerBudgetOptions()

    @Transactional
    override fun updateBudgetOptions(partnerId: Long, officeAdministrationFlatRate: Int?, staffCostsFlatRate: Int?) {
        budgetOptionsRepository.findById(partnerId).ifPresentOrElse(
            {
                it.officeAdministrationFlatRate = officeAdministrationFlatRate
                it.staffCostsFlatRate = staffCostsFlatRate
            },
            {
                budgetOptionsRepository.save(ProjectPartnerBudgetOptionsEntity(partnerId, officeAdministrationFlatRate, staffCostsFlatRate))
            }
        )
    }

    @Transactional
    override fun deleteBudgetOptions(partnerId: Long) =
        budgetOptionsRepository.deleteById(partnerId)

    @Transactional
    override fun deleteStaffCosts(partnerId: Long) =
        staffCostRepository.deleteAllByPartnerId(partnerId)

}
