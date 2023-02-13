package io.cloudflight.jems.server.project.service.report.partner.control.file.generateExport

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException


private const val GENERATE_REPORT_CONTROL_EXPORT_ERROR_CODE_PREFIX = "S-GCRE"
private const val GENERATE_REPORT_CONTROL_EXPORT_ERROR_KEY_PREFIX = "use.generate.control.report.export"

class GenerateReportControlExportException(cause: Throwable): ApplicationException(
    code = GENERATE_REPORT_CONTROL_EXPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GENERATE_REPORT_CONTROL_EXPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
