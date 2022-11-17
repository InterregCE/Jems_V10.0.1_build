package io.cloudflight.jems.server.project.service.customCostOptions.unitCost.deleteProjectUnitCost

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val DELETE_PROJECT_UNIT_COST_ERROR_CODE_PREFIX = "S-DPUC"
private const val DELETE_PROJECT_UNIT_COST_ERROR_KEY_PREFIX = "use.case.delete.project.unit.cost"

class DeleteProjectUnitCostException(cause: Throwable) : ApplicationException(
    code = DELETE_PROJECT_UNIT_COST_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_PROJECT_UNIT_COST_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class ProjectUnitCostNotFound : ApplicationNotFoundException(
    code = "$DELETE_PROJECT_UNIT_COST_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_PROJECT_UNIT_COST_ERROR_KEY_PREFIX.not.found"),
)

class ProjectUnitCostIsInUse : ApplicationUnprocessableException(
    code = "$DELETE_PROJECT_UNIT_COST_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$DELETE_PROJECT_UNIT_COST_ERROR_KEY_PREFIX.is.still.in.use"),
)
