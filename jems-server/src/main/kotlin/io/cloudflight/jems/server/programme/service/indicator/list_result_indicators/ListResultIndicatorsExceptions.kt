package io.cloudflight.jems.server.programme.service.indicator.list_result_indicators

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val LIST_RESULT_INDICATORS_ERROR_CODE_PREFIX = "S-IND-LRI"
const val LIST_RESULT_INDICATOR_ERROR_KEY_PREFIX = "use.case.list.result.indicators"

class GetResultIndicatorDetailsException(cause: Throwable) : ApplicationException(
    code = "$LIST_RESULT_INDICATORS_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$LIST_RESULT_INDICATOR_ERROR_KEY_PREFIX.get.details.failed"), cause = cause
)

class GetResultIndicatorSummariesException(cause: Throwable) : ApplicationException(
    code = "$LIST_RESULT_INDICATORS_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$LIST_RESULT_INDICATOR_ERROR_KEY_PREFIX.get.summaries.failed"), cause = cause
)

class GetResultIndicatorSummariesForSpecificObjectiveException(cause: Throwable) : ApplicationException(
    code = "$LIST_RESULT_INDICATORS_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$LIST_RESULT_INDICATOR_ERROR_KEY_PREFIX.get.summaries.for.specific.objective.failed"), cause = cause
)


