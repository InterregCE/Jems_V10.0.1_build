package io.cloudflight.jems.server.project.service.customCostOptions.unitCost.createProjectUnitCost

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val CREATE_PROJECT_UNIT_COST_ERROR_CODE_PREFIX = "S-CPUC"
private const val CREATE_PROJECT_UNIT_COST_ERROR_KEY_PREFIX = "use.case.create.project.unit.cost"

class CreateProjectUnitCostException(cause: Throwable) : ApplicationException(
    code = CREATE_PROJECT_UNIT_COST_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CREATE_PROJECT_UNIT_COST_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class ProjectDefinedUnitCostAreForbiddenForThisCall : ApplicationUnprocessableException(
    code = "$CREATE_PROJECT_UNIT_COST_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CREATE_PROJECT_UNIT_COST_ERROR_KEY_PREFIX.forbidden"),
)
