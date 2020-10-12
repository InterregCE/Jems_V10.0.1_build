package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetTravel
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerBudgetTravelRepository
    : ProjectPartnerBudgetCommonRepository<ProjectPartnerBudgetTravel>
