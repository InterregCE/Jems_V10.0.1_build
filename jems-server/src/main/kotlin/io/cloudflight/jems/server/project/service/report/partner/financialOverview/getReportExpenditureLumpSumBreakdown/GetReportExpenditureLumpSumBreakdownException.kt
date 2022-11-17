package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureLumpSumBreakdown

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_REPORT_LUMP_SUM_BREAKDOWN_ERROR_CODE_PREFIX = "S-GRLB"
private const val GET_REPORT_LUMP_SUM_BREAKDOWN_ERROR_KEY_PREFIX = "use.case.get.report.lumpSum.breakdown"

class GetReportExpenditureLumpSumBreakdownException(cause: Throwable) : ApplicationException(
    code = GET_REPORT_LUMP_SUM_BREAKDOWN_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_REPORT_LUMP_SUM_BREAKDOWN_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
