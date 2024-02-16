package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCertificateInvestmentsBreakdown

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_REPORT_CERTIFICATE_INVESTMENT_BREAKDOWN_ERROR_CODE_PREFIX = "S-GRCIB"
private const val GET_REPORT_CERTIFICATE_INVESTMENT_BREAKDOWN_ERROR_KEY_PREFIX = "use.case.get.report.expenditure.investment.breakdown"


class GetReportCertificateInvestmentBreakdownException(cause: Throwable) : ApplicationException(
    code = GET_REPORT_CERTIFICATE_INVESTMENT_BREAKDOWN_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_REPORT_CERTIFICATE_INVESTMENT_BREAKDOWN_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
