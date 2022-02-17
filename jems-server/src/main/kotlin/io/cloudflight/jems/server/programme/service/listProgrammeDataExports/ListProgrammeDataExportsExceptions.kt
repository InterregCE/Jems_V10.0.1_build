package io.cloudflight.jems.server.programme.service.listProgrammeDataExports

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val LIST_PROGRAMME_DATA_EXPORTS_ERROR_CODE_PREFIX = "S-LPDE"
private const val LIST_PROGRAMME_DATA_EXPORTS_ERROR_KEY_PREFIX = "use.case.list.programme.data.exports"

class ListProgrammeDataExportException(cause: Throwable) : ApplicationException(
    code = LIST_PROGRAMME_DATA_EXPORTS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$LIST_PROGRAMME_DATA_EXPORTS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
