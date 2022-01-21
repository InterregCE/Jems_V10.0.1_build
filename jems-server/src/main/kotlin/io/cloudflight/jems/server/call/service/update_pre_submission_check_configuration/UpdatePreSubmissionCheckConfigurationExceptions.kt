package io.cloudflight.jems.server.call.service.update_pre_submission_check_configuration

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val UPDATE_PRE_SUBMISSION_CHECK_SETTINGS_ERROR_CODE_PREFIX = "S-UPSC"
private const val UPDATE_PRE_SUBMISSION_CHECK_SETTINGS_ERROR_KEY_PREFIX =
    "use.case.update.pre.submission.check.settings"

class UpdatePreSubmissionCheckSettingsException(cause: Throwable) : ApplicationException(
    code = UPDATE_PRE_SUBMISSION_CHECK_SETTINGS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PRE_SUBMISSION_CHECK_SETTINGS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
