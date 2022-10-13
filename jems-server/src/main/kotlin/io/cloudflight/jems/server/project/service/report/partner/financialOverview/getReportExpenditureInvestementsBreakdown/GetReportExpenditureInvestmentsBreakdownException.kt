package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestementsBreakdown

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_REPORT_EXPENDITURE_BREAKDOWN_INVESTMENTS_ERROR_CODE_PREFIX = "S-GREBI"
private const val GET_REPORT_EXPENDITURE_BREAKDOWN_INVESTMENTS_ERROR_KEY_PREFIX = "use.case.get.report.expenditure.breakdown.investments"


class GetReportExpenditureInvestmentsBreakdownException(cause: Throwable) : ApplicationException(
    code = GET_REPORT_EXPENDITURE_BREAKDOWN_INVESTMENTS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_REPORT_EXPENDITURE_BREAKDOWN_INVESTMENTS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
