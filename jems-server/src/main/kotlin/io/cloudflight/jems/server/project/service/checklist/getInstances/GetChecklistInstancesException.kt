package io.cloudflight.jems.server.project.service.checklist.getInstances

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val GET_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX = "S-CHIN"
const val GET_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX = "use.case.get.checklist.instance"

class GetChecklistInstanceException(cause: Throwable) : ApplicationException(
    code = "$GET_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$GET_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.failed"), cause = cause
)

class GetChecklistInstanceDetailNotFoundException : ApplicationNotFoundException(
    code = "$GET_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$GET_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.detail.not.found"),
)

class GetChecklistDetailNotAllowedException : ApplicationUnprocessableException(
    code = "$GET_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$GET_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.not.allowed")
)
