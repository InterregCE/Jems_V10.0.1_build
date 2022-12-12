package io.cloudflight.jems.server.project.service.report.project.base.getProjectReportList

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_REPORT_LIST_ERROR_CODE_PREFIX = "S-GPRL"
private const val GET_PROJECT_REPORT_LIST_ERROR_KEY_PREFIX = "use.case.get.project.report.list"

class GetProjectReportListException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_REPORT_LIST_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_REPORT_LIST_ERROR_KEY_PREFIX.list.failed"),
    cause = cause,
)
