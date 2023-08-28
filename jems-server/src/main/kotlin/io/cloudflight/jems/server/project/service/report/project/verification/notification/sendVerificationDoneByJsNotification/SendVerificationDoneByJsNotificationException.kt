package io.cloudflight.jems.server.project.service.report.project.verification.notification.sendVerificationDoneByJsNotification

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val SEND_VERIFICATION_DONE_BY_JS_NOTIFICATION_ERROR_CODE_PREFIX = "S-SVDBJN"
private const val SEND_VERIFICATION_DONE_BY_JS_NOTIFICATION_ERROR_KEY_PREFIX = "use.case.send.verification.done.by.js.notification"

class SendVerificationDoneByJsNotificationException(cause: Throwable): ApplicationException(
    code = SEND_VERIFICATION_DONE_BY_JS_NOTIFICATION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$SEND_VERIFICATION_DONE_BY_JS_NOTIFICATION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class VerificationNotificationNotEnabledInCallException : ApplicationUnprocessableException(
    code = "$SEND_VERIFICATION_DONE_BY_JS_NOTIFICATION_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$SEND_VERIFICATION_DONE_BY_JS_NOTIFICATION_ERROR_KEY_PREFIX.notification.not.enabled"),
)
