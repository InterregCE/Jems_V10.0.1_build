package io.cloudflight.jems.server.project.repository.partner.budget.options

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetOptionsEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerBudgetOptionsRepository: CrudRepository<ProjectPartnerBudgetOptionsEntity, Long>
