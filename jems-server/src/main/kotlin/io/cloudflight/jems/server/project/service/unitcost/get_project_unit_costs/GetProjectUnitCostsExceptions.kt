package io.cloudflight.jems.server.project.service.unitcost.get_project_unit_costs

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_UNIT_COSTS_ERROR_CODE_PREFIX = "S-GPUC"
private const val GET_PROJECT_UNIT_COSTS_ERROR_KEY_PREFIX = "use.case.get.project.unit.costs"

class GetProjectUnitCostsException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_UNIT_COSTS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_UNIT_COSTS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)