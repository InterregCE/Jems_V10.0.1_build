package io.cloudflight.jems.server.project.service.report.partner.file.control.setDescriptionToControlReportFile

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val SET_DESCRIPTION_TO_CONTROL_REPORT_FILE_ERROR_CODE_PREFIX = "S-SDTPPCRF"
private const val SET_DESCRIPTION_TO_CONTROL_REPORT_FILE_ERROR_KEY_PREFIX = "use.case.set.description.to.project.partner.control.report.file"

class SetDescriptionToControlReportFileException(cause: Throwable) : ApplicationException(
    code = SET_DESCRIPTION_TO_CONTROL_REPORT_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$SET_DESCRIPTION_TO_CONTROL_REPORT_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
