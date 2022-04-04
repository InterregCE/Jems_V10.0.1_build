package io.cloudflight.jems.server.call.service.update_call_unit_costs

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val UPDATE_CALL_UNIT_COSTS_ERROR_CODE_PREFIX = "S-UPC-UC"
private const val UPDATE_CALL_UNIT_COSTS_ERROR_KEY_PREFIX = "use.case.update.call.unitCosts"

class UpdateCallUnitCostsExceptions(cause: Throwable) : ApplicationException(
    code = UPDATE_CALL_UNIT_COSTS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_CALL_UNIT_COSTS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class UnitCostNotFound : ApplicationNotFoundException(
    code = "$UPDATE_CALL_UNIT_COSTS_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_CALL_UNIT_COSTS_ERROR_KEY_PREFIX.unitCost.not.found"),
)

class UnitCostsRemovedAfterCallPublished : ApplicationBadRequestException(
    code = "$UPDATE_CALL_UNIT_COSTS_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_CALL_UNIT_COSTS_ERROR_KEY_PREFIX.removing.or.updating.existing.unitCosts.is.forbidden"),
)
