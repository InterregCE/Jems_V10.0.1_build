package io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.scope.getCorrectionCostItems

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val GET_CORRECTION_COST_ITEMS_ERROR_CODE_PREFIX = "S-GCCI"
private const val GET_CORRECTION_COST_ITEMS_ERROR_KEY_PREFIX = "use.case.get.correction.cost.items"

class GetCorrectionCostItemsException (cause: Throwable): ApplicationException(
    code = GET_CORRECTION_COST_ITEMS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_CORRECTION_COST_ITEMS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class MandatoryScopeNotSetException: ApplicationUnprocessableException(
    code = GET_CORRECTION_COST_ITEMS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_CORRECTION_COST_ITEMS_ERROR_KEY_PREFIX.mandatory.scope.not.set"),

    )