package io.cloudflight.jems.server.project.service.partner.budget

import io.cloudflight.jems.api.project.dto.partner.budget.InputBudget
import io.cloudflight.jems.server.project.entity.partner.budget.Budget
import io.cloudflight.jems.server.project.entity.partner.budget.CommonBudget
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetEquipment
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetExternal
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetInfrastructure
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetStaffCost
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetTravel
import java.math.BigDecimal

fun InputBudget.getNumberOfUnits(): BigDecimal = numberOfUnits.truncate()
fun InputBudget.getPricePerUnit(): BigDecimal = pricePerUnit.truncate()

private fun toBudget(
    description: String?,
    numberOfUnits: BigDecimal,
    pricePerUnit: BigDecimal
): Budget = Budget(
    description = description,
    numberOfUnits = numberOfUnits,
    pricePerUnit = pricePerUnit,
    rowSum = numberOfUnits.multiply(pricePerUnit).truncate()
)

fun InputBudget.toStaffCost(partnerId: Long) = ProjectPartnerBudgetStaffCost(
    id = id ?: 0,
    partnerId = partnerId,
    budget = toBudget(
        description = description,
        numberOfUnits = getNumberOfUnits(),
        pricePerUnit =  getPricePerUnit()
    )
)

fun InputBudget.toTravel(partnerId: Long) = ProjectPartnerBudgetTravel(
    id = id ?: 0,
    partnerId = partnerId,
    budget = toBudget(
        description = description,
        numberOfUnits = getNumberOfUnits(),
        pricePerUnit =  getPricePerUnit()
    )
)

fun InputBudget.toExternal(partnerId: Long) = ProjectPartnerBudgetExternal(
    id = id ?: 0,
    partnerId = partnerId,
    budget = toBudget(
        description = description,
        numberOfUnits = getNumberOfUnits(),
        pricePerUnit =  getPricePerUnit()
    )
)

fun InputBudget.toEquipment(partnerId: Long) = ProjectPartnerBudgetEquipment(
    id = id ?: 0,
    partnerId = partnerId,
    budget = toBudget(
        description = description,
        numberOfUnits = getNumberOfUnits(),
        pricePerUnit =  getPricePerUnit()
    )
)

fun InputBudget.toInfrastructure(partnerId: Long) = ProjectPartnerBudgetInfrastructure(
    id = id ?: 0,
    partnerId = partnerId,
    budget = toBudget(
        description = description,
        numberOfUnits = getNumberOfUnits(),
        pricePerUnit =  getPricePerUnit()
    )
)

fun CommonBudget.toOutput() = InputBudget(
    id = id,
    description = budget.description,
    numberOfUnits = budget.numberOfUnits,
    pricePerUnit = budget.pricePerUnit,
    rowSum = budget.rowSum
)
