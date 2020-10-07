package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.api.project.dto.partner.budget.InputBudget
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetStaffCost
import java.math.RoundingMode

fun InputBudget.toEntity(partnerId: Long) = ProjectPartnerBudgetStaffCost(
    id = id,
    partnerId = partnerId,
    numberOfUnits = numberOfUnits.setScale(2, RoundingMode.FLOOR),
    pricePerUnit = pricePerUnit.setScale(2, RoundingMode.FLOOR)
)

fun ProjectPartnerBudgetStaffCost.toOutput() = InputBudget(
    id = id,
    numberOfUnits = numberOfUnits,
    pricePerUnit = pricePerUnit
)
