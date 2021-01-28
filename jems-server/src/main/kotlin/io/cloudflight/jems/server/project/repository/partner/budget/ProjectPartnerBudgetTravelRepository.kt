package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.travel.ProjectPartnerBudgetTravelEntity
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerBudgetTravelRepository
    : ProjectPartnerBaseBudgetRepository<ProjectPartnerBudgetTravelEntity>
