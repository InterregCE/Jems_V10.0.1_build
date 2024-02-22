package io.cloudflight.jems.server.project.service.report.project.closure.getProjectClosure

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_REPORT_PROJECT_CLOSURE_ERROR_CODE_PREFIX = "S-GPRPCP"
private const val GET_PROJECT_REPORT_PROJECT_CLOSURE_ERROR_KEY_PREFIX = "use.case.get.project.report.project.closure"

class GetProjectReportProjectClosureException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_REPORT_PROJECT_CLOSURE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_REPORT_PROJECT_CLOSURE_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
