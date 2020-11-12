package io.cloudflight.jems.server.project.service.budget.get_project_budget

import io.cloudflight.jems.server.project.service.budget.model.PartnerBudget

interface GetProjectBudgetInteractor {
    fun getBudget(projectId: Long): List<PartnerBudget>
}
