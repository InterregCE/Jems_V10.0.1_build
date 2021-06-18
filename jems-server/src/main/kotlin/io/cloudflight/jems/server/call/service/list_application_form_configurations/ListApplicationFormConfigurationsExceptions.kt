package io.cloudflight.jems.server.call.service.list_application_form_configurations

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val LIST_APPLICATION_FORM_CONFIGURATIONS_ERROR_CODE_PREFIX = "S-LAFC"
private const val LIST_APPLICATION_FORM_CONFIGURATIONS_ERROR_KEY_PREFIX = "use.case.list.application.form.configurations"

class ListApplicationFormConfigurationsException(cause: Throwable) : ApplicationException(
    code = LIST_APPLICATION_FORM_CONFIGURATIONS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$LIST_APPLICATION_FORM_CONFIGURATIONS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
