package io.cloudflight.jems.server.project.service.application.revert_application_to_contracted

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val REVERT_APPLICATION_TO_CLOSED_ERROR_CODE_PREFIX = "S-RAC"
private const val REVERT_APPLICATION_TO_CLOSED_ERROR_KEY_PREFIX = "use.case.revert.application.to.contracted"

class RevertApplicationToContractedException(cause: Throwable) : ApplicationException(
    code = REVERT_APPLICATION_TO_CLOSED_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$REVERT_APPLICATION_TO_CLOSED_ERROR_KEY_PREFIX.failed"), cause = cause
)
