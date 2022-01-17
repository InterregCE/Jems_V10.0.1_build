package io.cloudflight.jems.server.notification.mail.service.send_mail_on_jems_mail_event

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val SEND_MAIL_ERROR_CODE_PREFIX = "S-SM"
private const val SEND_MAIL_ERROR_KEY_PREFIX = "use.case.send.mail"

class SendMailException(cause: Throwable) : ApplicationException(
    code = SEND_MAIL_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$SEND_MAIL_ERROR_KEY_PREFIX.failed"), cause = cause
)
