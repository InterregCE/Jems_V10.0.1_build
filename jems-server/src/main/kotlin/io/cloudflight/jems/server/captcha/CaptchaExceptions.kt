package io.cloudflight.jems.server.captcha

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationFailedDependencyException

const val CAPTCHA_ERROR_CODE_PREFIX = "S-P"
const val CAPTCHA_ERROR_KEY_PREFIX = "captcha"

class CaptchaNotEncodedException : ApplicationFailedDependencyException(
    code = "$CAPTCHA_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CAPTCHA_ERROR_KEY_PREFIX.not.encoded")
)
