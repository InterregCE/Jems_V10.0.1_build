package io.cloudflight.jems.server.project.service.contracting.monitoring.updateProjectContractingMonitoring

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val UPDATE_CONTRACTING_MONITORING_ERROR_CODE_PREFIX = "S-UPCMO"
private const val UPDATE_CONTRACTING_MONITORING_ERROR_KEY_PREFIX = "use.case.update.project.contracting.monitoring"

class UpdateContractingMonitoringException(cause: Throwable) : ApplicationException(
    code = UPDATE_CONTRACTING_MONITORING_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_CONTRACTING_MONITORING_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
