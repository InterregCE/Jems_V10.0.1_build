package io.cloudflight.jems.server.project.service.partner.budget.updateBudgetUnitCosts

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_BUDGET_UNIT_COSTS_ERROR_CODE_PREFIX = "S-UBUC"
private const val UPDATE_BUDGET_UNIT_COSTS_ERROR_KEY_PREFIX = "use.case.update.budget.unit.costs"

class UpdateBudgetUnitCostsException(cause: Throwable) : ApplicationException(
    code = UPDATE_BUDGET_UNIT_COSTS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_BUDGET_UNIT_COSTS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class UnitCostsBudgetSectionIsNotAllowed : ApplicationUnprocessableException(
    code = "$UPDATE_BUDGET_UNIT_COSTS_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_BUDGET_UNIT_COSTS_ERROR_KEY_PREFIX.no.any.multiple.categories.unit.cost.allowed"),
)

class UnitCostCannotBeFound(unitCostId: Long) : ApplicationUnprocessableException(
    code = "$UPDATE_BUDGET_UNIT_COSTS_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_BUDGET_UNIT_COSTS_ERROR_KEY_PREFIX.unit.cost.cannot.be.found"),
    message = "Unit cost id=$unitCostId cannot be found"
)
