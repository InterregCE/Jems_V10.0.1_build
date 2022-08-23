package io.cloudflight.jems.server.project.service.customCostOptions.unitCost.getProjectAvailableUnitCost

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_AVAILABLE_UNIT_COSTS_ERROR_CODE_PREFIX = "S-GPAUC"
private const val GET_PROJECT_AVAILABLE_UNIT_COSTS_ERROR_KEY_PREFIX = "use.case.get.project.available.unit.cost"

class GetProjectAvailableUnitCostException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_AVAILABLE_UNIT_COSTS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_AVAILABLE_UNIT_COSTS_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
