package io.cloudflight.jems.server.project.service.application.get_possible_status_to_revert_to

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val GET_POSSIBLE_STATUS_TO_RETURN_TO_ERROR_CODE_PREFIX = "S-PA-GST"
const val GET_POSSIBLE_STATUS_TO_RETURN_TO_ERROR_KEY_PREFIX = "use.case.get.possible.status.to.revert.to"

class GetPossibleStatusToRevertToException(cause: Throwable) : ApplicationException(
    code = GET_POSSIBLE_STATUS_TO_RETURN_TO_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_POSSIBLE_STATUS_TO_RETURN_TO_ERROR_KEY_PREFIX.failed"), cause = cause
)
