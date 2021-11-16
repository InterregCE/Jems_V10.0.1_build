package io.cloudflight.jems.server.project.service.export.export_budget

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val EXPORT_BUDGET_ERROR_CODE_PREFIX = "S-EB"
private const val EXPORT_BUDGET_ERROR_KEY_PREFIX = "use.case.export.budget"

class ExportBudgetException(cause: Throwable) : ApplicationException(
    code = EXPORT_BUDGET_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$EXPORT_BUDGET_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
