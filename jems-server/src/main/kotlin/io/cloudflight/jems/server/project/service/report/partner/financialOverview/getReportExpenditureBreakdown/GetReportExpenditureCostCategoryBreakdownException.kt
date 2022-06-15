package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_REPORT_EXPENDITURE_BREAKDOWN_ERROR_CODE_PREFIX = "S-GREB"
private const val GET_REPORT_EXPENDITURE_BREAKDOWN_ERROR_KEY_PREFIX = "use.case.get.report.expenditure.breakdown"

class GetReportExpenditureCostCategoryBreakdownException(cause: Throwable) : ApplicationException(
    code = GET_REPORT_EXPENDITURE_BREAKDOWN_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_REPORT_EXPENDITURE_BREAKDOWN_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
