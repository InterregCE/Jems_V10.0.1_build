package io.cloudflight.jems.server.call.service.publish_call

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val PUBLISH_CALL_ERROR_CODE_PREFIX = "S-PPC"
private const val PUBLISH_CALL_ERROR_KEY_PREFIX = "use.case.publish.call"

class PublishCallException(cause: Throwable) : ApplicationException(
    code = PUBLISH_CALL_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$PUBLISH_CALL_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
