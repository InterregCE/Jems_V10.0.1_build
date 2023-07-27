package io.cloudflight.jems.server.project.service.report.project.verification.expenditure.updateProjectReportVerificationExpenditureRiskBased

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val UPDATE_PROJECT_REPORT_VERIFICATION_EXPENDITURE_RISK_BASED_ERROR_CODE_PREFIX = "S-UPRVERB"
private const val UPDATE_PROJECT_REPORT_VERIFICATION_EXPENDITURE_RISK_BASED_ERROR_KEY_PREFIX =
    "use.case.update.project.report.verification.expenditure.risk.based"

class UpdateProjectReportVerificationExpenditureRiskBasedException(cause: Throwable) : ApplicationException(
    code = UPDATE_PROJECT_REPORT_VERIFICATION_EXPENDITURE_RISK_BASED_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PROJECT_REPORT_VERIFICATION_EXPENDITURE_RISK_BASED_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
