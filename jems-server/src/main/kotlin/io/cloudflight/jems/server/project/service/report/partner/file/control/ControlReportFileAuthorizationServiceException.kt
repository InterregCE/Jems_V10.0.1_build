package io.cloudflight.jems.server.project.service.report.partner.file.control

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationAuthenticationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val CONTROL_REPORT_FILE_AUTH_SERVICE_ERROR_CODE_PREFIX = "S-CRFAS"
private const val CONTROL_REPORT_FILE_AUTH_SERVICE_ERROR_KEY_PREFIX = "control.report.file.auth.service"

class ReportNotInControl : ApplicationUnprocessableException(
    code = "$CONTROL_REPORT_FILE_AUTH_SERVICE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CONTROL_REPORT_FILE_AUTH_SERVICE_ERROR_KEY_PREFIX.report.not.in.control"),
)

class FileNotFound : ApplicationNotFoundException(
    code = "$CONTROL_REPORT_FILE_AUTH_SERVICE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$CONTROL_REPORT_FILE_AUTH_SERVICE_ERROR_KEY_PREFIX.not.found"),
)

class UserIsNotOwnerOfFile : ApplicationAuthenticationException(
    code = "$CONTROL_REPORT_FILE_AUTH_SERVICE_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$CONTROL_REPORT_FILE_AUTH_SERVICE_ERROR_KEY_PREFIX.user.is.not.owner"),
)
