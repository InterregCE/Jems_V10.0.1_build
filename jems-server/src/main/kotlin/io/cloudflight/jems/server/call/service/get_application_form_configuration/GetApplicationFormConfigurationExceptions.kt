package io.cloudflight.jems.server.call.service.get_application_form_configuration

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_APPLICATION_FORM_CONFIGURATION_ERROR_CODE_PREFIX = "S-GAFC"
private const val GET_APPLICATION_FORM_CONFIGURATION_ERROR_KEY_PREFIX = "use.case.get.application.form.configuration"

class GetApplicationFormConfigurationException(cause: Throwable) : ApplicationException(
    code = GET_APPLICATION_FORM_CONFIGURATION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_APPLICATION_FORM_CONFIGURATION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
