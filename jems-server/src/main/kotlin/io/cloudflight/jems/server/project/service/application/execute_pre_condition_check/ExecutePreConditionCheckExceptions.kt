package io.cloudflight.jems.server.project.service.application.execute_pre_condition_check

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val EXECUTE_PRE_CONDITION_CHECK_ERROR_CODE_PREFIX = "S-EPA-PCC"
const val EXECUTE_PRE_CONDITION_CHECK_ERROR_KEY_PREFIX = "use.case.execute.pre.condition.check"

class ExecutePreConditionCheckException(cause: Throwable) : ApplicationException(
    code = EXECUTE_PRE_CONDITION_CHECK_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$EXECUTE_PRE_CONDITION_CHECK_ERROR_KEY_PREFIX.failed"), cause = cause
)
