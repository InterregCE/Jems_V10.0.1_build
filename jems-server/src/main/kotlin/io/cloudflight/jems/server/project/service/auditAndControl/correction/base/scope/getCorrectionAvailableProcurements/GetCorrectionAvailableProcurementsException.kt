package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.scope.getCorrectionAvailableProcurements

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val GET_CORRECTION_AVAILABLE_PROCUREMENTS_ERROR_CODE_PREFIX = "S-GCAP"
private const val GET_CORRECTION_AVAILABLE_ERROR_KEY_PREFIX = "use.case.get.correction.available.procurements"

class GetCorrectionAvailableProcurementsException (cause: Throwable): ApplicationException(
    code = GET_CORRECTION_AVAILABLE_PROCUREMENTS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_CORRECTION_AVAILABLE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class MandatoryScopeNotSetException: ApplicationUnprocessableException(
code = GET_CORRECTION_AVAILABLE_PROCUREMENTS_ERROR_CODE_PREFIX,
i18nMessage = I18nMessage("$GET_CORRECTION_AVAILABLE_ERROR_KEY_PREFIX.mandatory.scope.not.set"),

)
