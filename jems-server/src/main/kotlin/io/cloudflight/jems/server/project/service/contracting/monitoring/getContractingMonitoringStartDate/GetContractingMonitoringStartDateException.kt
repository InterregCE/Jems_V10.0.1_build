package io.cloudflight.jems.server.project.service.contracting.monitoring.getContractingMonitoringStartDate

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_CONTRACTING_MONITORING_START_DATE_ERROR_CODE_PREFIX = "S-CMSD"
private const val GET_CONTRACTING_MONITORING_START_DATE_ERROR_KEY_PREFIX = "use.case.get.contracting.monitoring.start.date"

class GetContractingMonitoringStartDateException(cause: Throwable) : ApplicationException(
    code = GET_CONTRACTING_MONITORING_START_DATE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_CONTRACTING_MONITORING_START_DATE_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
