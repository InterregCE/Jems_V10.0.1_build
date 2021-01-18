package io.cloudflight.jems.server.project.service.workpackage.investment.get_project_investment_summaries

import io.cloudflight.jems.server.project.service.workpackage.model.InvestmentSummary

interface GetProjectInvestmentSummariesInteractor {
    fun getProjectInvestmentSummaries(projectId: Long): List<InvestmentSummary>
}
