package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.unit_cost.ProjectPartnerBudgetUnitCostEntity
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerBudgetUnitCostRepository :
    ProjectPartnerBaseBudgetRepository<ProjectPartnerBudgetUnitCostEntity>
