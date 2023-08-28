package io.cloudflight.jems.server.project.service.report.project.verification.notification.getProjectReportVerificationNotification

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val SEND_VERIFICATION_DONE_BY_JS_NOTIFICATION_ERROR_CODE_PREFIX = "S-SVDBJN"
private const val SEND_VERIFICATION_DONE_BY_JS_NOTIFICATION_ERROR_KEY_PREFIX = "use.case.send.verification.done.by.js.notification"

class GetProjectReportVerificationNotificationException(cause: Throwable): ApplicationException(
    code = SEND_VERIFICATION_DONE_BY_JS_NOTIFICATION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$SEND_VERIFICATION_DONE_BY_JS_NOTIFICATION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
