package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetStaffCostEntity
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerBudgetStaffCostRepository
    : ProjectPartnerBaseBudgetRepository<ProjectPartnerBudgetStaffCostEntity>
