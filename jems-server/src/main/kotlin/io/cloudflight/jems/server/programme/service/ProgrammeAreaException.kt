package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationAccessDeniedException

const val UPDATE_PROGRAMME_AREA_ERROR_CODE_PREFIX = "S-UPA"
const val UPDATE_PROGRAMME_AREA_ERROR_KEY_PREFIX = "use.case.update.programme.area"

class UpdateProgrammeAreasWhenProgrammeSetupRestricted : ApplicationAccessDeniedException(
    code = "$UPDATE_PROGRAMME_AREA_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_PROGRAMME_AREA_ERROR_KEY_PREFIX.programme.setup.restricted"),
)