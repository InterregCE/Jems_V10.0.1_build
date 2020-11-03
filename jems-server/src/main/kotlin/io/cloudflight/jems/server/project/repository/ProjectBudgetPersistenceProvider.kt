package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetOptionsRepository
import io.cloudflight.jems.server.project.repository.partner.budget.ProjectPartnerBudgetStaffCostRepository
import io.cloudflight.jems.server.project.service.partner.budget.ProjectBudgetPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectBudgetPersistenceProvider(
    private val budgetOptionsRepository: ProjectPartnerBudgetOptionsRepository,
    private val staffCostRepository: ProjectPartnerBudgetStaffCostRepository
) : ProjectBudgetPersistence {

    @Transactional(readOnly = true)
    override fun getBudgetOptions(partnerId: Long): ProjectPartnerBudgetOptions? =
        budgetOptionsRepository.findById(partnerId).orElse(null)

    @Transactional
    override fun updateBudgetOptions(partnerId: Long, officeAdministrationFlatRate: Int?, staffCostsFlatRate: Int?) {
        budgetOptionsRepository.findById(partnerId).ifPresentOrElse(
            {
                it.officeAdministrationFlatRate = officeAdministrationFlatRate
                it.staffCostsFlatRate = staffCostsFlatRate
            },
            {
                budgetOptionsRepository.save(ProjectPartnerBudgetOptions(partnerId, officeAdministrationFlatRate, staffCostsFlatRate))
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
