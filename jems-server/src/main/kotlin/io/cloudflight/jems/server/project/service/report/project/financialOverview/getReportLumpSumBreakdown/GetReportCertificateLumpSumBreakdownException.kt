package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportLumpSumBreakdown

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_REPORT_CERTIFICATE_LUMP_SUM_BREAKDOWN_ERROR_CODE_PREFIX = "S-GRCLSB"
private const val GET_REPORT_CERTIFICATE_LUMP_SUM_BREAKDOWN_ERROR_KEY_PREFIX = "use.case.get.report.expenditure.lump.sum.breakdown"


class GetReportCertificateLumpSumBreakdownException(cause: Throwable) : ApplicationException(
    code = GET_REPORT_CERTIFICATE_LUMP_SUM_BREAKDOWN_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_REPORT_CERTIFICATE_LUMP_SUM_BREAKDOWN_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
