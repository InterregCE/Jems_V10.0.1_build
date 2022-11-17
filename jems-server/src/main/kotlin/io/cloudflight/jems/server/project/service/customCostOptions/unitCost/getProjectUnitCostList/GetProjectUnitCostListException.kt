package io.cloudflight.jems.server.project.service.customCostOptions.unitCost.getProjectUnitCostList

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_UNIT_COST_LIST_ERROR_CODE_PREFIX = "S-GPUCL"
private const val GET_PROJECT_UNIT_COST_LIST_ERROR_KEY_PREFIX = "use.case.get.project.unit.cost.list"

private const val GET_PROJECT_UNIT_COST_DETAIL_ERROR_CODE_PREFIX = "S-GPUCD"
private const val GET_PROJECT_UNIT_COST_DETAIL_ERROR_KEY_PREFIX = "use.case.get.project.unit.cost.detail"

class GetProjectUnitCostListException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_UNIT_COST_LIST_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_UNIT_COST_LIST_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class GetProjectUnitCostDetailException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_UNIT_COST_DETAIL_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_UNIT_COST_DETAIL_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
