package io.cloudflight.jems.server.programme.service.indicator.list_output_indicators

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val LIST_OUTPUT_INDICATORS_ERROR_CODE_PREFIX = "S-IND-LOI"
const val LIST_OUTPUT_INDICATOR_ERROR_KEY_PREFIX = "use.case.list.output.indicators"

class GetOutputIndicatorDetailsException(cause: Throwable) : ApplicationException(
    code = "$LIST_OUTPUT_INDICATORS_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$LIST_OUTPUT_INDICATOR_ERROR_KEY_PREFIX.get.details.failed"), cause = cause
)

class GetOutputIndicatorSummariesException(cause: Throwable) : ApplicationException(
    code = "$LIST_OUTPUT_INDICATORS_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$LIST_OUTPUT_INDICATOR_ERROR_KEY_PREFIX.get.summaries.failed"), cause = cause
)

class GetOutputIndicatorSummariesForSpecificObjectiveException(cause: Throwable) : ApplicationException(
    code = "$LIST_OUTPUT_INDICATORS_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$LIST_OUTPUT_INDICATOR_ERROR_KEY_PREFIX.get.summaries.for.specific.objective.failed"), cause = cause
)


