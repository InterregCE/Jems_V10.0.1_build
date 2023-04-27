package io.cloudflight.jems.server.project.service.report.project.export

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val PROJECT_REPORT_EXPORT_ERROR_CODE_PREFIX = "S-PRPRE"
private const val PROJECT_REPORT_EXPORT_ERROR_KEY_PREFIX = "use.case.export.project.report"

class ProjectReportExportException(cause: Throwable) : ApplicationException(
    code = PROJECT_REPORT_EXPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$PROJECT_REPORT_EXPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
