package io.cloudflight.jems.server.project.service.report.project.verification.getProjectReportVerificationConclusion

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val GET_CONCLUSION_VERIFICATION_PROJECT_REPORT_ERROR_CODE_PREFIX = "S-GCNVPR"
private const val GET_CONCLUSION_VERIFICATION_REPORT_ERROR_KEY_PREFIX = "use.case.get.conclusion.verification.project.report"

class GetProjectReportVerificationConclusionException(cause: Throwable): ApplicationException(
    code = GET_CONCLUSION_VERIFICATION_PROJECT_REPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_CONCLUSION_VERIFICATION_REPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class ReportVerificationStatusNotValidException : ApplicationUnprocessableException(
    code = "$GET_CONCLUSION_VERIFICATION_PROJECT_REPORT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$GET_CONCLUSION_VERIFICATION_REPORT_ERROR_KEY_PREFIX.report.status.not.valid")
)