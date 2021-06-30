package io.cloudflight.jems.server.call.service.get_application_form_field_configurations

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_APPLICATION_FORM_FIELD_CONFIGURATIONS_ERROR_CODE_PREFIX = "S-GAFC"
private const val GET_APPLICATION_FORM_FIELD_CONFIGURATION_ERROR_KEY_PREFIX = "use.case.get.application.form.field.configurations"

class GetApplicationFormConfigurationException(cause: Throwable) : ApplicationException(
    code = GET_APPLICATION_FORM_FIELD_CONFIGURATIONS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_APPLICATION_FORM_FIELD_CONFIGURATION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
