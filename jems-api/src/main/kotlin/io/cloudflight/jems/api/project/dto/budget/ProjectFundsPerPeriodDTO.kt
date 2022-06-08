package io.cloudflight.jems.api.project.dto.budget

data class ProjectFundsPerPeriodDTO(
    val managementFundsPerPeriod: Set<ProjectFundBudgetPerPeriodDTO>,
    val spfFundsPerPeriod: Set<ProjectFundBudgetPerPeriodDTO>
)
