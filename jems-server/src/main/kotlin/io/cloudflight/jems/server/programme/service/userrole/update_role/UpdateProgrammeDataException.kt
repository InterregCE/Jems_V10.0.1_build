package io.cloudflight.jems.server.programme.service.userrole.update_role

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val UPDATE_PROGRAMME_DATA_ERROR_CODE_PREFIX = "S-UPD"
const val UPDATE_PROGRAMME_DATA_ERROR_KEY_PREFIX = "use.case.update.programme.data"

class UpdateDefaultUserRoleFailed(cause: Throwable) : ApplicationException(
    code = UPDATE_PROGRAMME_DATA_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PROGRAMME_DATA_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
