package io.cloudflight.jems.server.programme.service.stateaid.update_stateaid

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val UPDATE_STATE_AIDS_ERROR_CODE_PREFIX = "S-USA"
const val UPDATE_STATE_AIDS_ERROR_KEY_PREFIX = "use.case.update.state.aid"

class UpdateStateAidsFailedException(cause: Throwable) : ApplicationException(
    code = UPDATE_STATE_AIDS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_STATE_AIDS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class DeletionIsNotAllowedException : ApplicationUnprocessableException(
    code = "$UPDATE_STATE_AIDS_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_STATE_AIDS_ERROR_KEY_PREFIX.deletion.is.not.allowed"),
)

class MeasureChangeIsNotAllowed : ApplicationUnprocessableException(
    code = "$UPDATE_STATE_AIDS_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_STATE_AIDS_ERROR_KEY_PREFIX.measure.change.is.not.allowed"),
)

class MaxAllowedStateAidsReachedException(maxAmount: Int) :
    ApplicationUnprocessableException(
        code = "$UPDATE_STATE_AIDS_ERROR_CODE_PREFIX-003",
        i18nMessage = I18nMessage(
            "$UPDATE_STATE_AIDS_ERROR_KEY_PREFIX.max.allowed.amount.reached",
            mapOf("maxSize" to maxAmount.toString())
        ),
        message = "max allowed: $maxAmount",
    )
