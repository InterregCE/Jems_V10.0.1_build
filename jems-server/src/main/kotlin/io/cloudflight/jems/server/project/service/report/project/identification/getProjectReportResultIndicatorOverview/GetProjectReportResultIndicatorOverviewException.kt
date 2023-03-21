package io.cloudflight.jems.server.project.service.report.project.identification.getProjectReportResultIndicatorOverview

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_REPORT_RESULT_INDICATOR_OVERVIEW_ERROR_CODE_PREFIX = "S-GPRRIO"
private const val GET_PROJECT_REPORT_RESULT_INDICATOR_OVERVIEW_ERROR_KEY_PREFIX = "use.case.get.project.report.result.indicator.overview"

class GetProjectReportResultIndicatorOverviewException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_REPORT_RESULT_INDICATOR_OVERVIEW_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_REPORT_RESULT_INDICATOR_OVERVIEW_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
