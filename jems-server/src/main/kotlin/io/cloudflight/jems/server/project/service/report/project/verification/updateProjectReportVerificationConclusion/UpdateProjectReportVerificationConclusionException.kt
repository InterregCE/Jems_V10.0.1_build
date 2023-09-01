package io.cloudflight.jems.server.project.service.report.project.verification.updateProjectReportVerificationConclusion

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_CONCLUSION_VERIFICATION_PROJECT_REPORT_ERROR_CODE_PREFIX = "S-UCNVPR"
private const val UPDATE_CONCLUSION_VERIFICATION_REPORT_ERROR_KEY_PREFIX = "use.case.update.conclusion.verification.project.report"

class UpdateProjectReportVerificationConclusionException(cause: Throwable): ApplicationException (
    code = UPDATE_CONCLUSION_VERIFICATION_PROJECT_REPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_CONCLUSION_VERIFICATION_REPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class ReportVerificationStatusNotValidException : ApplicationUnprocessableException(
    code = "$UPDATE_CONCLUSION_VERIFICATION_PROJECT_REPORT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_CONCLUSION_VERIFICATION_REPORT_ERROR_KEY_PREFIX.report.status.not.valid")
)

class ReportVerificationInvalidInputException : ApplicationUnprocessableException(
    code = "$UPDATE_CONCLUSION_VERIFICATION_PROJECT_REPORT_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_CONCLUSION_VERIFICATION_REPORT_ERROR_KEY_PREFIX.invalid.input")
)
