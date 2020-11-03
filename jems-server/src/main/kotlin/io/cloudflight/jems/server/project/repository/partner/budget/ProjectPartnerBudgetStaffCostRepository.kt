package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetStaffCost
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerBudgetStaffCostRepository
    : ProjectPartnerBudgetCommonRepository<ProjectPartnerBudgetStaffCost> {
    fun deleteAllByPartnerId(partnerId: Long)
}
