package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.api.project.dto.partner.budget.InputBudget
import io.cloudflight.jems.server.project.entity.partner.budget.Budget
import io.cloudflight.jems.server.project.entity.partner.budget.CommonBudget
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetStaffCost
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetTravel
import java.math.RoundingMode

fun InputBudget.toStaffCost(partnerId: Long) = ProjectPartnerBudgetStaffCost(
    id = id,
    partnerId = partnerId,
    budget = Budget(
        description = description,
        numberOfUnits = numberOfUnits.setScale(2, RoundingMode.FLOOR),
        pricePerUnit = pricePerUnit.setScale(2, RoundingMode.FLOOR)
    )
)

fun InputBudget.toTravel(partnerId: Long) = ProjectPartnerBudgetTravel(
    id = id,
    partnerId = partnerId,
    budget = Budget(
        description = description,
        numberOfUnits = numberOfUnits.setScale(2, RoundingMode.FLOOR),
        pricePerUnit = pricePerUnit.setScale(2, RoundingMode.FLOOR)
    )
)

fun CommonBudget.toOutput() = InputBudget(
    id = id,
    description = budget!!.description,
    numberOfUnits = budget!!.numberOfUnits,
    pricePerUnit = budget!!.pricePerUnit
)
