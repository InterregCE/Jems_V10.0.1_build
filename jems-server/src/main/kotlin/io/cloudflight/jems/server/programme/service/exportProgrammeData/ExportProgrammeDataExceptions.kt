package io.cloudflight.jems.server.programme.service.exportProgrammeData

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val EXPORT_PROGRAMME_DATA_ERROR_CODE_PREFIX = "S-EPD"
private const val EXPORT_PROGRAMME_DATA_ERROR_KEY_PREFIX = "use.case.export.programme.data"

class ExportProgrammeDataException(cause: Throwable) : ApplicationException(
    code = EXPORT_PROGRAMME_DATA_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$EXPORT_PROGRAMME_DATA_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
class ExportInProgressException : ApplicationUnprocessableException(
    code = "$EXPORT_PROGRAMME_DATA_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$EXPORT_PROGRAMME_DATA_ERROR_KEY_PREFIX.an.export.is.already.in.progress")
)
