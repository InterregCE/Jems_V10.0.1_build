package io.cloudflight.jems.server.project.service.overview.getIndicatorLivingTableOverview

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_OVERVIEW_RESULT_INDICATOR_LIVING_TABLE_ERROR_CODE_PREFIX = "S-GPORILT"
private const val GET_PROJECT_OVERVIEW_RESULT_INDICATOR_LIVING_TABLE_ERROR_KEY_PREFIX = "use.case.get.project.overview.result.indicator.living.table"

class GetResultIndicatorLivingTableException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_OVERVIEW_RESULT_INDICATOR_LIVING_TABLE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_OVERVIEW_RESULT_INDICATOR_LIVING_TABLE_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
