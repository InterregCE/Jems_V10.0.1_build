package io.cloudflight.jems.server.project.service.auditAndControl.correction.measure.update

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val UPDATE_PROGRAMME_MEASURE_ERROR_CODE_PREFIX = "S-UPM"
private const val UPDATE_PROGRAMME_MEASURE_PREFIX = "use.case.update.programme.measure"

class UpdateProgrammeMeasureException(cause: Throwable): ApplicationException(
    code = UPDATE_PROGRAMME_MEASURE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PROGRAMME_MEASURE_PREFIX.failed"),
    cause = cause
)
