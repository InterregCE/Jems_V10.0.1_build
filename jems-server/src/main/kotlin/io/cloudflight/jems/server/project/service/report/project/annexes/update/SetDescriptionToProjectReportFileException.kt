package io.cloudflight.jems.server.project.service.report.project.annexes.update

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val SET_DESCRIPTION_TO_PROJECT_REPORT_FILE_ERROR_CODE_PREFIX = "S-SDTPRF"
private const val SET_DESCRIPTION_TO_PROJECT_REPORT_FILE_ERROR_KEY_PREFIX = "use.case.set.description.to.project.report.file"

class SetDescriptionToProjectReportFileException(cause: Throwable) : ApplicationException(
    code = SET_DESCRIPTION_TO_PROJECT_REPORT_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$SET_DESCRIPTION_TO_PROJECT_REPORT_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
