package io.cloudflight.jems.server.call.service.update_application_form_field_configuration

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_APPLICATION_FORM_FIELD_CONFIGURATIONS_ERROR_CODE_PREFIX = "S-UAFC"
private const val UPDATE_APPLICATION_FORM_FIELD_CONFIGURATIONS_ERROR_KEY_PREFIX = "use.case.update.application.form.field.configurations"

class UpdateApplicationFormFieldConfigurationsException(cause: Throwable) : ApplicationException(
    code = UPDATE_APPLICATION_FORM_FIELD_CONFIGURATIONS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_APPLICATION_FORM_FIELD_CONFIGURATIONS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class InvalidFieldStatusException : ApplicationUnprocessableException(
    code = "$UPDATE_APPLICATION_FORM_FIELD_CONFIGURATIONS_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_APPLICATION_FORM_FIELD_CONFIGURATIONS_ERROR_KEY_PREFIX.invalid.field.status"),
)

class InvalidFieldVisibilityChangeWhenCallIsPublishedException : ApplicationUnprocessableException(
    code = "$UPDATE_APPLICATION_FORM_FIELD_CONFIGURATIONS_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_APPLICATION_FORM_FIELD_CONFIGURATIONS_ERROR_KEY_PREFIX.invalid.field.visibility.change.when.call.is.published"),
)

