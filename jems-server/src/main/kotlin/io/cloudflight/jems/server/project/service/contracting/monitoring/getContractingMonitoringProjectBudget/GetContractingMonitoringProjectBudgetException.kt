package io.cloudflight.jems.server.project.service.contracting.monitoring.getContractingMonitoringProjectBudget

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_CONTRACTING_MONITORING_PROJECT_BUDGET_ERROR_CODE_PREFIX = "S-CMPB"
private const val GET_CONTRACTING_MONITORING_PROJECT_BUDGET_ERROR_KEY_PREFIX = "use.case.get.contracting.monitoring.project.budget"

class GetContractingMonitoringProjectBudgetException(cause: Throwable) : ApplicationException(
    code = GET_CONTRACTING_MONITORING_PROJECT_BUDGET_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_CONTRACTING_MONITORING_PROJECT_BUDGET_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
