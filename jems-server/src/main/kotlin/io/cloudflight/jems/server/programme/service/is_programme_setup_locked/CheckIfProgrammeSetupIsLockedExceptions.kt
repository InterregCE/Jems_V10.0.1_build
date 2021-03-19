package io.cloudflight.jems.server.programme.service.is_programme_setup_locked

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val IS_PROGRAMME_SETUP_LOCKED_ERROR_CODE_PREFIX = "S-PSL"
private const val IS_PROGRAMME_SETUP_LOCKED_ERROR_KEY_PREFIX = "use.case.is.programme.setup.locked"

class IsProgrammeSetupLockedException(cause: Throwable) : ApplicationException(
    code = IS_PROGRAMME_SETUP_LOCKED_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$IS_PROGRAMME_SETUP_LOCKED_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
