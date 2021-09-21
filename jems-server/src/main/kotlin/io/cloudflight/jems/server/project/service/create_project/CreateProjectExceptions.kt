package io.cloudflight.jems.server.project.service.create_project

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val CREATE_PROJECT_ERROR_CODE_PREFIX = "S-CP"
private const val CREATE_PROJECT_ERROR_KEY_PREFIX = "use.case.create.project"

class CreateProjectExceptions(cause: Throwable) : ApplicationException(
    code = CREATE_PROJECT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CREATE_PROJECT_ERROR_KEY_PREFIX.failed"), cause = cause
)

class CallNotFound : ApplicationBadRequestException(
    code = "$CREATE_PROJECT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("call.not.found"),
)

class CallNotOpen : ApplicationBadRequestException(
    code = "$CREATE_PROJECT_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("call.not.open"),
)
