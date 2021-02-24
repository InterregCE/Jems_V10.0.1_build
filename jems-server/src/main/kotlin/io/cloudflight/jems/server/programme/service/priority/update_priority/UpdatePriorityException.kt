package io.cloudflight.jems.server.programme.service.priority.update_priority

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException

private const val UPDATE_PRIORITY_ERROR_CODE_PREFIX = "S-UPP"
private const val UPDATE_PRIORITY_ERROR_KEY_PREFIX = "use.case.update.programme.priority"

class UpdateWhenProgrammeSetupRestricted : ApplicationBadRequestException(
    code = "$UPDATE_PRIORITY_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_PRIORITY_ERROR_KEY_PREFIX.programme.setup.restricted"),
)
