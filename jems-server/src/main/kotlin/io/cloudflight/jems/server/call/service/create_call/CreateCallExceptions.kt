package io.cloudflight.jems.server.call.service.create_call

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val CREATE_CALL_ERROR_CODE_PREFIX = "S-CPC"
private const val CREATE_CALL_ERROR_KEY_PREFIX = "use.case.create.call"

class CreateCallException(cause: Throwable) : ApplicationException(
    code = CREATE_CALL_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CREATE_CALL_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class CallNameNotUnique : ApplicationUnprocessableException(
    code = "$CREATE_CALL_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CREATE_CALL_ERROR_KEY_PREFIX.name.not.unique"),
)

class CallCreatedIsNotDraft : ApplicationUnprocessableException(
    code = "$CREATE_CALL_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$CREATE_CALL_ERROR_KEY_PREFIX.not.draft"),
)
