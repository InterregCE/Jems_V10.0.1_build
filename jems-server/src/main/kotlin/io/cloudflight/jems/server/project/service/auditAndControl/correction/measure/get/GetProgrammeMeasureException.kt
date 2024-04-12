package io.cloudflight.jems.server.project.service.auditAndControl.correction.measure.get

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROGRAMME_MEASURE_ERROR_CODE_PREFIX = "S-GPM"
private const val GET_PROGRAMME_MEASURE_PREFIX = "use.case.get.programme.measure"

class GetProgrammeMeasureException(cause: Throwable): ApplicationException(
    code = GET_PROGRAMME_MEASURE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROGRAMME_MEASURE_PREFIX.failed"),
    cause = cause
)

