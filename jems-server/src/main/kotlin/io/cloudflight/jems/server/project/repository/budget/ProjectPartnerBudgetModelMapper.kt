package io.cloudflight.jems.server.project.repository.budget

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetRow
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetView
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerCost

fun ProjectPartnerBudgetView.toProjectPartnerBudget() = ProjectPartnerCost(
    partnerId = partnerId,
    sum = sum
)

fun List<ProjectPartnerBudgetView>.toProjectPartnerBudget() = map { it.toProjectPartnerBudget() }

fun ProjectPartnerBudgetRow.toProjectPartnerBudget() = ProjectPartnerCost(
    partnerId = partnerId,
    sum = sum
)

fun List<ProjectPartnerBudgetRow>.toProjectPartnerBudgetHistoricalData() = map { it.toProjectPartnerBudget() }