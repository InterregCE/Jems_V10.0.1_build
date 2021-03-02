package io.cloudflight.jems.server.programme.service.costoption

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationAccessDeniedException


const val UPDATE_UNIT_COST_ERROR_CODE_PREFIX = "S-UUC"
const val UPDATE_UNIT_COST_ERROR_KEY_PREFIX = "use.case.update.unitcost"

class UpdateUnitCostWhenProgrammeSetupRestricted : ApplicationAccessDeniedException(
    code = "$UPDATE_UNIT_COST_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_UNIT_COST_ERROR_KEY_PREFIX.programme.setup.restricted"),
)

 class DeleteUnitCostWhenProgrammeSetupRestricted : ApplicationAccessDeniedException(
    code = "$UPDATE_UNIT_COST_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_UNIT_COST_ERROR_KEY_PREFIX.programme.setup.restricted"),
)

