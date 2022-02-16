package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException

private const val UPDATE_PROJECT_ERROR_CODE_PREFIX = "S-UPP"
private const val UPDATE_PROJECT_ERROR_KEY_PREFIX = "use.case.update.project"

class UpdateRestrictedFieldsWhenProjectContracted : ApplicationBadRequestException(
    code = "$UPDATE_PROJECT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_PROJECT_ERROR_KEY_PREFIX.changes.not.allowed.when.project.is.contracted"),
    message = "When project is contracted, only limited set of fields can be updated!"
)
