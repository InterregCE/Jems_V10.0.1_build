package io.cloudflight.jems.server.project.service.application.set_application_to_contracted

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val SET_APPLICATION_TO_CONTRACTED_ERROR_CODE_PREFIX = "S-SAC"
private const val SET_APPLICATION_TO_CONTRACTED_ERROR_KEY_PREFIX = "use.case.set.application.to.contracted"

class SetApplicationToContractedException(cause: Throwable) : ApplicationException(
    code = SET_APPLICATION_TO_CONTRACTED_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$SET_APPLICATION_TO_CONTRACTED_ERROR_KEY_PREFIX.failed"), cause = cause
)
