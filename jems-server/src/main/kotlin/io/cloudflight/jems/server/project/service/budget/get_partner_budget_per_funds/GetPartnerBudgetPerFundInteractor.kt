package io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_funds

import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund

interface GetPartnerBudgetPerFundInteractor {
    fun getProjectPartnerBudgetPerFund(projectId: Long, version: String?): List<ProjectPartnerBudgetPerFund>
}
