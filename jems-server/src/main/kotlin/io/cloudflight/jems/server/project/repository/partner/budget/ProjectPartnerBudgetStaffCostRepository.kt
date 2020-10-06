package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetStaffCost
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerBudgetStaffCostRepository : CrudRepository<ProjectPartnerBudgetStaffCost, Long> {

    fun findAllByPartnerIdOrderByIdAsc(partnerId: Long): List<ProjectPartnerBudgetStaffCost>

}
