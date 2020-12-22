package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetExternalEntity
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerBudgetExternalRepository
    : ProjectPartnerBudgetCommonRepository<ProjectPartnerBudgetExternalEntity>
