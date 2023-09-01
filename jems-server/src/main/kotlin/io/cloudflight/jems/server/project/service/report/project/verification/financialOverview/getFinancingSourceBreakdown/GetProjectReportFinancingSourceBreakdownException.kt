package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_REPORT_FINANCING_SOURCE_BREAKDOWN_ERROR_CODE_PREFIX = "S-GPRFSB"
private const val GET_PROJECT_REPORT_FINANCING_SOURCE_BREAKDOWN_ERROR_KEY_PREFIX = "use.case.get.project.report.verification.financing.source.breakdown"

class GetProjectReportFinancingSourceBreakdownException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_REPORT_FINANCING_SOURCE_BREAKDOWN_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_REPORT_FINANCING_SOURCE_BREAKDOWN_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
