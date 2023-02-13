package io.cloudflight.jems.server.project.service.report.partner.control.file.listFiles

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val LIST_REPORT_CONTROL_FILES_ERROR_CODE_PREFIX = "S-LRCF"
private const val LIST_REPORT_CONTROL_FILES_ERROR_KEY_PREFIX = "use.case.list.report.control.files"

class ListReportControlFilesException(cause: Throwable) : ApplicationException(
    code = LIST_REPORT_CONTROL_FILES_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$LIST_REPORT_CONTROL_FILES_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
