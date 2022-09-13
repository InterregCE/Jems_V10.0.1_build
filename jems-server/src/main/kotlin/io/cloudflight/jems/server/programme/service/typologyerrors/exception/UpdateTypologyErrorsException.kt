package io.cloudflight.jems.server.programme.service.typologyerrors.exception

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val UPDATE_TYPOLOGY_ERRORS_ERROR_CODE_PREFIX = "S-UTE"
const val UPDATE_TYPOLOGY_ERRORS_ERROR_KEY_PREFIX = "use.case.update.typology.errors"

class UpdateTypologyErrorsFailedException(cause: Throwable) : ApplicationException(
    code = UPDATE_TYPOLOGY_ERRORS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_TYPOLOGY_ERRORS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class MaxAllowedTypologyErrorsReachedException(maxAmount: Int) :
    ApplicationUnprocessableException(
        code = "$UPDATE_TYPOLOGY_ERRORS_ERROR_CODE_PREFIX-001",
        i18nMessage = I18nMessage(
            "$UPDATE_TYPOLOGY_ERRORS_ERROR_KEY_PREFIX.max.allowed.amount.reached",
            mapOf("maxSize" to maxAmount.toString())
        ),
        message = "max allowed: $maxAmount",
    )

class DeletionIsNotAllowedException : ApplicationUnprocessableException(
    code = "$UPDATE_TYPOLOGY_ERRORS_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_TYPOLOGY_ERRORS_ERROR_KEY_PREFIX.deletion.is.not.allowed"),
)
