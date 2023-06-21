package io.cloudflight.jems.server.project.service.report.project.base.runProjectReportPreSubmissionCheck

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val RUN_PROJECT_REPORT_PRE_SUBMISSION_CHECK_PROJECT_REPORT_ERROR_CODE_PREFIX = "S-RPRPCHPR"
private const val RUN_PROJECT_REPORT_PRE_SUBMISSION_CHECK_PROJECT_REPORT_ERROR_KEY_PREFIX = "use.case.run.project.report.pre.submission.check.on.project.report"

class RunProjectReportPreSubmissionCheckException(cause: Throwable) : ApplicationException(
    code = RUN_PROJECT_REPORT_PRE_SUBMISSION_CHECK_PROJECT_REPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$RUN_PROJECT_REPORT_PRE_SUBMISSION_CHECK_PROJECT_REPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class ReportNotFound : ApplicationNotFoundException(
    code = "$RUN_PROJECT_REPORT_PRE_SUBMISSION_CHECK_PROJECT_REPORT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$RUN_PROJECT_REPORT_PRE_SUBMISSION_CHECK_PROJECT_REPORT_ERROR_KEY_PREFIX.report.not.found"),
)

class PreCheckPluginFailed(cause: Throwable) : ApplicationUnprocessableException(
    code = "$RUN_PROJECT_REPORT_PRE_SUBMISSION_CHECK_PROJECT_REPORT_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$RUN_PROJECT_REPORT_PRE_SUBMISSION_CHECK_PROJECT_REPORT_ERROR_KEY_PREFIX.plugin.failed"),
    cause = cause,
)
