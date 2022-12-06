package io.cloudflight.jems.server.project.service.checklist.getInstances.control

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

const val GET_CONTROL_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX = "S-C-CHIN"
const val GET_CONTROL_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX = "use.case.get.control.checklist.instance"

class GetControlChecklistInstanceException(cause: Throwable) : ApplicationException(
    code = GET_CONTROL_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_CONTROL_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.failed"), cause = cause
)

class GetControlChecklistInstanceDetailNotFoundException : ApplicationNotFoundException(
    code = "$GET_CONTROL_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$GET_CONTROL_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.detail.not.found")
)
