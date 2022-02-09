package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.spf.ProjectPartnerBudgetSpfCostEntity
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerBudgetSpfCostRepository
    : ProjectPartnerBaseBudgetRepository<ProjectPartnerBudgetSpfCostEntity>
