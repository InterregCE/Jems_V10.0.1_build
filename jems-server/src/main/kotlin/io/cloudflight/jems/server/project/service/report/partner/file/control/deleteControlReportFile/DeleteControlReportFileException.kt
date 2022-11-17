package io.cloudflight.jems.server.project.service.report.partner.file.control.deleteControlReportFile

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationAuthenticationException
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val DELETE_CONTROL_REPORT_FILE_ERROR_CODE_PREFIX = "S-DPPCRF"
private const val DELETE_CONTROL_REPORT_FILE_ERROR_KEY_PREFIX = "use.case.delete.project.partner.control.report.file"

class DeleteControlReportFileException(cause: Throwable) : ApplicationException(
    code = DELETE_CONTROL_REPORT_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_CONTROL_REPORT_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
