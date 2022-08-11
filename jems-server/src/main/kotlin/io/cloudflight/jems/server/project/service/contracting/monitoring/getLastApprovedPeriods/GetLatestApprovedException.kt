package io.cloudflight.jems.server.project.service.contracting.monitoring.getLastApprovedPeriods

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_LATEST_APPROVED_PERIODS_ERROR_CODE_PREFIX = "S-GLAP"
private const val GET_LATEST_APPROVED_PERIODS_ERROR_KEY_PREFIX = "use.case.get.latest.approved.periods"

class GetLatestApprovedException(cause: Throwable) : ApplicationException(
    code = GET_LATEST_APPROVED_PERIODS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_LATEST_APPROVED_PERIODS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
