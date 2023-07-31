package io.cloudflight.jems.server.project.service.report.project.verification.getProjectReportVerificationClarification

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val GET_CLARIFICATION_VERIFICATION_PROJECT_REPORT_ERROR_CODE_PREFIX = "S-GCLVPR"
private const val GET_CLARIFICATION_VERIFICATION_REPORT_ERROR_KEY_PREFIX = "use.case.get.clarification.verification.project.report"

class GetProjectReportVerificationClarificationException(cause: Throwable): ApplicationException(
    code = GET_CLARIFICATION_VERIFICATION_PROJECT_REPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_CLARIFICATION_VERIFICATION_REPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class ReportVerificationStatusNotValidException : ApplicationUnprocessableException(
    code = "$GET_CLARIFICATION_VERIFICATION_PROJECT_REPORT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$GET_CLARIFICATION_VERIFICATION_REPORT_ERROR_KEY_PREFIX.report.status.not.valid")
)
