package io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_CONTRACTING_MONITORING_ERROR_CODE_PREFIX = "S-GPCMO"
private const val GET_CONTRACTING_MONITORING_ERROR_KEY_PREFIX = "use.case.get.project.contracting.monitoring"

class GetContractingMonitoringException(cause: Throwable) : ApplicationException(
    code = GET_CONTRACTING_MONITORING_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_CONTRACTING_MONITORING_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
