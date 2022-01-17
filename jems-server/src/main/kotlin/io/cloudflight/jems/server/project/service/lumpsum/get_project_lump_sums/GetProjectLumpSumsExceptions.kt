package io.cloudflight.jems.server.project.service.lumpsum.get_project_lump_sums

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_LUMP_SUMS_ERROR_CODE_PREFIX = "S-GPLS"
private const val GET_PROJECT_LUMPSUMS_ERROR_KEY_PREFIX = "use.case.get.project.lump.sums"

class GetProjectLumpSumsException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_LUMP_SUMS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_LUMPSUMS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
