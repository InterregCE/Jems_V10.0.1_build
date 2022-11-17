package io.cloudflight.jems.server.programme.service.typologyerrors.exception

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val GET_TYPOLOGY_ERRORS_ERROR_CODE_PREFIX = "S-GTE"
const val GET_TYPOLOGY_ERRORS_ERROR_KEY_PREFIX = "use.case.get.typology.errors"

class GetTypologyErrorsFailedException(cause: Throwable) : ApplicationException(
    code = GET_TYPOLOGY_ERRORS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_TYPOLOGY_ERRORS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
