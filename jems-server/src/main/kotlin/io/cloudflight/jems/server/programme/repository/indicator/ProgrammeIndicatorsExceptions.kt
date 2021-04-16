package io.cloudflight.jems.server.programme.repository.indicator

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

const val INDICATORS_PERSISTENCE_ERROR_CODE_PREFIX = "R-PRS-ORI"

class OutputIndicatorNotFoundException : ApplicationNotFoundException(
    code = "$INDICATORS_PERSISTENCE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("output.indicator.not.found"), cause = null
)
class ResultIndicatorNotFoundException : ApplicationNotFoundException(
    code = "$INDICATORS_PERSISTENCE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("result.indicator.not.found"), cause = null
)

