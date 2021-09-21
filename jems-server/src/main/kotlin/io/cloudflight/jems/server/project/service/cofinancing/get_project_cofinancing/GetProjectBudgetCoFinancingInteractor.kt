package io.cloudflight.jems.server.project.service.cofinancing.get_project_cofinancing

import io.cloudflight.jems.server.project.service.cofinancing.model.PartnerBudgetCoFinancing

interface GetProjectBudgetCoFinancingInteractor {
    fun getBudgetCoFinancing(projectId: Long, version: String? = null): List<PartnerBudgetCoFinancing>
}