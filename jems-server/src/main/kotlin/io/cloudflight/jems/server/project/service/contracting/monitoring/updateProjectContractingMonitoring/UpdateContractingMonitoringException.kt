package io.cloudflight.jems.server.project.service.contracting.monitoring.updateProjectContractingMonitoring

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_CONTRACTING_MONITORING_ERROR_CODE_PREFIX = "S-UPCMO"
private const val UPDATE_CONTRACTING_MONITORING_ERROR_KEY_PREFIX = "use.case.update.project.contracting.monitoring"

class UpdateContractingMonitoringException(cause: Throwable) : ApplicationException(
    code = UPDATE_CONTRACTING_MONITORING_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_CONTRACTING_MONITORING_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class UpdateContractingMonitoringFTLSHasInstallmentsException : ApplicationUnprocessableException(
    code = "$UPDATE_CONTRACTING_MONITORING_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_CONTRACTING_MONITORING_ERROR_KEY_PREFIX.already.has.instalments"),
)

class UpdateContractingMonitoringFTLSLinkedToEcPaymentException : ApplicationUnprocessableException(
    code = "$UPDATE_CONTRACTING_MONITORING_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_CONTRACTING_MONITORING_ERROR_KEY_PREFIX.payment.linked.to.ecPayment"),
)
