package io.cloudflight.jems.server.project.service.result.get_project_result_indicators_overview

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_RESULT_INDICATORS_OVERVIEW_ERROR_CODE_PREFIX = "S-GPRIO"
private const val GET_PROJECT_RESULT_INDICATORS_OVERVIEW_ERROR_KEY_PREFIX = "use.case.get.project.results.indicators.overview"

class GetProjectResultIndicatorsOverviewException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_RESULT_INDICATORS_OVERVIEW_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_RESULT_INDICATORS_OVERVIEW_ERROR_KEY_PREFIX.failed"), cause = cause
)
