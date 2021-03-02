package io.cloudflight.jems.server.programme.service.strategy

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationAccessDeniedException

const val UPDATE_STRATEGY_ERROR_CODE_PREFIX = "S-US"
const val UPDATE_STRATEGY_ERROR_KEY_PREFIX = "use.case.update.strategies"

class UpdateStrategiesWhenProgrammeSetupRestricted : ApplicationAccessDeniedException(
    code = "$UPDATE_STRATEGY_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_STRATEGY_ERROR_KEY_PREFIX.programme.setup.restricted"),
)