package io.cloudflight.jems.server.call.service.update_call

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_CALL_ERROR_CODE_PREFIX = "S-UPC"
private const val UPDATE_CALL_ERROR_KEY_PREFIX = "use.case.update.call"

class UpdateCallException(cause: Throwable) : ApplicationException(
    code = UPDATE_CALL_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_CALL_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class CallNameNotUnique : ApplicationUnprocessableException(
    code = "$UPDATE_CALL_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_CALL_ERROR_KEY_PREFIX.name.not.unique"),
)

class CallStatusChangeForbidden : ApplicationBadRequestException(
    code = "$UPDATE_CALL_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$UPDATE_CALL_ERROR_KEY_PREFIX.status.change.forbidden"),
    message = "Call Status cannot be updated by regular update! There are other special endpoints."
)

class UpdateRestrictedFieldsWhenCallPublished : ApplicationBadRequestException(
    code = "$UPDATE_CALL_ERROR_CODE_PREFIX-004",
    i18nMessage = I18nMessage("$UPDATE_CALL_ERROR_KEY_PREFIX.changes.not.allowed.when.call.is.published"),
    message = "When call is publish, only limited set of fields can be updated!"
)

class UpdatingEndDateIntoPast : ApplicationUnprocessableException(
    code = "$UPDATE_CALL_ERROR_CODE_PREFIX-005",
    i18nMessage = I18nMessage("$UPDATE_CALL_ERROR_KEY_PREFIX.endDate.changed.into.past"),
    message = "When changing Call endDate, time cannot be in the past."
)
