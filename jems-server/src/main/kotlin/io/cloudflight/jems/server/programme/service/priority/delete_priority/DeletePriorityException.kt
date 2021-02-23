package io.cloudflight.jems.server.programme.service.priority.delete_priority

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException

private const val DELETE_PRIORITY_ERROR_CODE_PREFIX = "S-DPP"
private const val DELETE_PRIORITY_ERROR_KEY_PREFIX = "use.case.delete.programme.priority"

class DeletionWhenProgrammeSetupRestricted : ApplicationBadRequestException(
    code = "$DELETE_PRIORITY_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_PRIORITY_ERROR_KEY_PREFIX.programme.setup.restricted"),
)
