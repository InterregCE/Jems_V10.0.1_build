package io.cloudflight.jems.server.programme.service.priority.delete_priority

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val DELETE_PRIORITY_ERROR_CODE_PREFIX = "S-DPP"
private const val DELETE_PRIORITY_ERROR_KEY_PREFIX = "use.case.delete.programme.priority"

class DeletePriorityFailed(cause: Throwable) : ApplicationException(
    code = DELETE_PRIORITY_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_PRIORITY_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class DeletionWhenProgrammeSetupRestricted : ApplicationBadRequestException(
    code = "$DELETE_PRIORITY_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_PRIORITY_ERROR_KEY_PREFIX.programme.setup.restricted"),
)

class ToDeletePriorityAlreadyUsedInCall : ApplicationBadRequestException(
    code = "$DELETE_PRIORITY_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$DELETE_PRIORITY_ERROR_KEY_PREFIX.already.used.in.call"),
)

class ToDeletePriorityAlreadyUsedInResultIndicator : ApplicationBadRequestException(
    code = "$DELETE_PRIORITY_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$DELETE_PRIORITY_ERROR_KEY_PREFIX.already.used.in.result.indicator"),
)

class ToDeletePriorityAlreadyUsedInOutputIndicator : ApplicationBadRequestException(
    code = "$DELETE_PRIORITY_ERROR_CODE_PREFIX-004",
    i18nMessage = I18nMessage("$DELETE_PRIORITY_ERROR_KEY_PREFIX.already.used.in.output.indicator"),
)
