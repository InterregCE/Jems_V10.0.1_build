package io.cloudflight.jems.server.project.service.model.project_funds_per_period

data class ProjectFundsPerPeriod(
    val managementFundsPerPeriod: List<ProjectFundBudgetPerPeriod>,
    val spfFundsPerPeriod: List<ProjectFundBudgetPerPeriod>
)
