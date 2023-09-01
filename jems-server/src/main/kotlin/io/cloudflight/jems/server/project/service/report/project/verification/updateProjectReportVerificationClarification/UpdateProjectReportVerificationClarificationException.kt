package io.cloudflight.jems.server.project.service.report.project.verification.updateProjectReportVerificationClarification

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_CLARIFICATION_VERIFICATION_PROJECT_REPORT_ERROR_CODE_PREFIX = "S-UCLVPR"
private const val UPDATE_CLARIFICATION_VERIFICATION_REPORT_ERROR_KEY_PREFIX = "use.case.update.clarification.verification.project.report"
class UpdateProjectReportVerificationClarificationException(cause: Throwable): ApplicationException(
    code = UPDATE_CLARIFICATION_VERIFICATION_PROJECT_REPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_CLARIFICATION_VERIFICATION_REPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class ReportVerificationStatusNotValidException : ApplicationUnprocessableException(
    code = "$UPDATE_CLARIFICATION_VERIFICATION_PROJECT_REPORT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_CLARIFICATION_VERIFICATION_REPORT_ERROR_KEY_PREFIX.report.status.not.valid")
)

class ReportVerificationInvalidInputException : ApplicationUnprocessableException(
    code = "$UPDATE_CLARIFICATION_VERIFICATION_PROJECT_REPORT_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_CLARIFICATION_VERIFICATION_REPORT_ERROR_KEY_PREFIX.invalid.input")
)
