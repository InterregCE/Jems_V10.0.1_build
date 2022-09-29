package io.cloudflight.jems.server.programme.service.costoption.deleteUnitCost

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationAccessDeniedException
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException
import io.cloudflight.jems.server.common.exception.ApplicationException

const val DELETE_UNIT_COST_ERROR_CODE_PREFIX = "S-UNC-DUC"
const val DELETE_UNIT_COST_ERROR_KEY_PREFIX = "use.case.delete.unit.cost"

class DeleteUnitCostFailed(cause: Throwable): ApplicationException(
    code = DELETE_UNIT_COST_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_UNIT_COST_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class DeleteUnitCostWhenProgrammeSetupRestricted: ApplicationAccessDeniedException(
    code = "$DELETE_UNIT_COST_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_UNIT_COST_ERROR_KEY_PREFIX.programme.setup.restricted")
)

class ToDeleteUnitCostAlreadyUsedInCall: ApplicationBadRequestException(
    code = "$DELETE_UNIT_COST_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$DELETE_UNIT_COST_ERROR_KEY_PREFIX.already.used.in.call")
)
