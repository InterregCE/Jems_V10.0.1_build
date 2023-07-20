package io.cloudflight.jems.server.project.service.report.project.base.finalizeVerification

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val FINALIZE_VERIFICATION_PROJECT_REPORT_ERROR_CODE_PREFIX = "S-FVPR"
private const val FINALIZE_VERIFICATION_REPORT_ERROR_KEY_PREFIX = "use.case.finalize.verification.project.report"

class FinalizeVerificationProjectReportException(cause: Throwable) : ApplicationException(
    code = FINALIZE_VERIFICATION_PROJECT_REPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$FINALIZE_VERIFICATION_REPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class ReportVerificationNotStartedException : ApplicationUnprocessableException(
    code = "$FINALIZE_VERIFICATION_PROJECT_REPORT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$FINALIZE_VERIFICATION_REPORT_ERROR_KEY_PREFIX.report.verification.not.started"),
)
