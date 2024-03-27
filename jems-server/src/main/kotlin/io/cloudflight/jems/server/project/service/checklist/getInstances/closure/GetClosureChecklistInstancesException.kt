package io.cloudflight.jems.server.project.service.checklist.getInstances.closure

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

const val GET_CLOSURE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX = "S-C-CHIN"
const val GET_CLOSURE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX = "use.case.get.closure.checklist.instance"

class GetClosureChecklistInstanceException(cause: Throwable) : ApplicationException(
    code = GET_CLOSURE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_CLOSURE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.failed"), cause = cause
)

class GetClosureChecklistInstanceDetailNotFoundException : ApplicationNotFoundException(
    code = "$GET_CLOSURE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$GET_CLOSURE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.detail.not.found")
)
