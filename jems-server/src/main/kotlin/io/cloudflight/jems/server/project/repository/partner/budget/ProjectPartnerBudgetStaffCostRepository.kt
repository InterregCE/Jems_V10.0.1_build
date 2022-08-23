package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.staff_cost.ProjectPartnerBudgetStaffCostEntity
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerBudgetStaffCostRepository :
    ProjectPartnerBaseBudgetRepository<ProjectPartnerBudgetStaffCostEntity> {

    fun existsByUnitCostId(unitCostId: Long): Boolean

}
