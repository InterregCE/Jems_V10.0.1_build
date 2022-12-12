package io.cloudflight.jems.server.project.service.report.project.base.getProjectReport

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_REPORT_ERROR_CODE_PREFIX = "S-GPR"
private const val GET_PROJECT_REPORT_ERROR_KEY_PREFIX = "use.case.get.project.report"

class GetProjectReportException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_REPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_REPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
