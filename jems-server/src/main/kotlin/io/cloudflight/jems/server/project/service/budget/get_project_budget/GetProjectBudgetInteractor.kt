package io.cloudflight.jems.server.project.service.budget.get_project_budget

import io.cloudflight.jems.server.project.service.budget.model.PartnerBudget
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import org.springframework.data.domain.Pageable

interface GetProjectBudgetInteractor {
    fun getBudget(projectId: Long, version: String? = null): List<PartnerBudget>
    fun getBudget(partners: List<ProjectPartnerSummary>, projectId: Long, version: String?): List<PartnerBudget>
}
