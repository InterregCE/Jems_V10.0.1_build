package io.cloudflight.jems.server.project.service.report.project.base.startVerificationProjectReport

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val START_VERIFICATION_PROJECT_REPORT_ERROR_CODE_PREFIX = "S-SVPR"
private const val START_VERIFICATION_REPORT_ERROR_KEY_PREFIX = "use.case.start.verification.project.report"

class StartVerificationProjectReportException(cause: Throwable) : ApplicationException(
    code = START_VERIFICATION_PROJECT_REPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$START_VERIFICATION_REPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class ReportNotSubmitted : ApplicationUnprocessableException(
    code = "$START_VERIFICATION_PROJECT_REPORT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$START_VERIFICATION_REPORT_ERROR_KEY_PREFIX.report.not.submitted"),
)