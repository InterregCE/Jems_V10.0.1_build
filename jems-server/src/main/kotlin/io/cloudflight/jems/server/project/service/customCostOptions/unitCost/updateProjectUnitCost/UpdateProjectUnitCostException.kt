package io.cloudflight.jems.server.project.service.customCostOptions.unitCost.updateProjectUnitCost

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val UPDATE_PROJECT_UNIT_COST_ERROR_CODE_PREFIX = "S-CPUC"
private const val UPDATE_PROJECT_UNIT_COST_ERROR_KEY_PREFIX = "use.case.update.project.unit.cost"

class UpdateProjectUnitCostException(cause: Throwable) : ApplicationException(
    code = UPDATE_PROJECT_UNIT_COST_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PROJECT_UNIT_COST_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
