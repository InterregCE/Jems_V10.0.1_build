package io.cloudflight.jems.server.project.repository.budget

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetEntity
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerCost

fun ProjectPartnerBudgetEntity.toProjectPartnerBudget() = ProjectPartnerCost(
    partnerId = partnerId,
    sum = sum
)

fun List<ProjectPartnerBudgetEntity>.toProjectPartnerBudget() = map { it.toProjectPartnerBudget() }
