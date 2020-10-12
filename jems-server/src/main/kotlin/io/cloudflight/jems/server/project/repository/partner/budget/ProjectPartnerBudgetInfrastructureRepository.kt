package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetInfrastructure
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerBudgetInfrastructureRepository
    : ProjectPartnerBudgetCommonRepository<ProjectPartnerBudgetInfrastructure>
