package io.cloudflight.jems.server.programme.service.costoption

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationAccessDeniedException


const val UPDATE_LUMP_SUM_ERROR_CODE_PREFIX = "S-ULS"
const val UPDATE_LUMP_SUM_ERROR_KEY_PREFIX = "use.case.update.lumpsum"

class UpdateLumpSumWhenProgrammeSetupRestricted : ApplicationAccessDeniedException(
    code = "$UPDATE_LUMP_SUM_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_LUMP_SUM_ERROR_KEY_PREFIX.programme.setup.restricted"),
)

 class DeleteLumpSumWhenProgrammeSetupRestricted : ApplicationAccessDeniedException(
    code = "$UPDATE_LUMP_SUM_ERROR_KEY_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_LUMP_SUM_ERROR_KEY_PREFIX.programme.setup.restricted"),
)

