package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetInfrastructureEntity
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerBudgetInfrastructureRepository
    : ProjectPartnerBudgetCommonRepository<ProjectPartnerBudgetInfrastructureEntity>
