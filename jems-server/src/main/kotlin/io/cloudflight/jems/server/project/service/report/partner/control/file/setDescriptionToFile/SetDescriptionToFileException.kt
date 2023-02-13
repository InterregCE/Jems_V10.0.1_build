package io.cloudflight.jems.server.project.service.report.partner.control.file.setDescriptionToFile

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val SET_DESCRIPTION_TO_REPORT_CONTROL_FILE_ERROR_CODE_PREFIX = "S-SDTRCF"
private const val SET_DESCRIPTION_TO_REPORT_CONTROL_FILE_ERROR_KEY_PREFIX = "use.case.set.description.to.report.control.file"

class SetDescriptionToFileException(cause: Throwable): ApplicationException (
    code = SET_DESCRIPTION_TO_REPORT_CONTROL_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$SET_DESCRIPTION_TO_REPORT_CONTROL_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)


