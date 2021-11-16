package io.cloudflight.jems.server.project.service.export.export_application_form

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val EXPORT_APPLICATION_FORM_ERROR_CODE_PREFIX = "S-EAF"
private const val EXPORT_APPLICATION_FORM_ERROR_KEY_PREFIX = "use.case.export.application.form"

class ExportApplicationFormException(cause: Throwable) : ApplicationException(
    code = EXPORT_APPLICATION_FORM_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$EXPORT_APPLICATION_FORM_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
