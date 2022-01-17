package io.cloudflight.jems.server.project.service.get_project_previous_status

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val GET_PROJECT_PREVIOUS_STATUS_ERROR_CODE_PREFIX = "S-GPS"
const val GET_PROJECT_PREVIOUS_STATUS_ERROR_KEY_PREFIX = "use.case.get.project.previous.status"


class GetProjectPreviousStatusExceptions(cause: Throwable): ApplicationException (
    code = GET_PROJECT_PREVIOUS_STATUS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_PREVIOUS_STATUS_ERROR_KEY_PREFIX.failed"), cause = cause
)
