package io.cloudflight.jems.server.project.service.report.partner.file.control.listProjectPartnerControlReportFile

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val LIST_PROJECT_PARTNER_CONTROL_REPORT_FILE_ERROR_CODE_PREFIX = "S-LPPCRF"
private const val LIST_PROJECT_PARTNER_CONTROL_REPORT_FILE_ERROR_KEY_PREFIX = "use.case.list.project.partner.control.report.file"

class ListControlReportFileException(cause: Throwable) : ApplicationException(
    code = LIST_PROJECT_PARTNER_CONTROL_REPORT_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$LIST_PROJECT_PARTNER_CONTROL_REPORT_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
