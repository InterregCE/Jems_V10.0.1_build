package io.cloudflight.jems.server.project.service.report.project.verification.expenditure.getProjectReportVerificationExpenditureRiskBased

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_REPORT_VERIFICATION_EXPENDITURE_RISK_BASED_ERROR_CODE_PREFIX = "S-GPRVERB"
private const val GET_PROJECT_REPORT_VERIFICATION_EXPENDITURE_RISK_BASED_ERROR_KEY_PREFIX = "use.case.get.project.report.verification.expenditure.risk.based"

class GetProjectReportVerificationExpenditureRiskBasedException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_REPORT_VERIFICATION_EXPENDITURE_RISK_BASED_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_REPORT_VERIFICATION_EXPENDITURE_RISK_BASED_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
