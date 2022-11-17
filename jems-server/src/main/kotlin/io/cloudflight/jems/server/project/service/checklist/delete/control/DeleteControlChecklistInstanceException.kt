package io.cloudflight.jems.server.project.service.checklist.delete.control

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException
import io.cloudflight.jems.server.project.service.checklist.update.control.UPDATE_CONTROL_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX
import io.cloudflight.jems.server.project.service.checklist.update.control.UPDATE_CONTROL_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX

const val DELETE_CONTROL_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX = "S-CHID"
const val DELETE_CONTROL_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX = "use.case.delete.control.checklist.instance"

class DeleteControlChecklistInstanceException(cause: Throwable) : ApplicationException(
    code = "$DELETE_CONTROL_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_CONTROL_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.failed"), cause = cause
)

class DeleteControlChecklistInstanceStatusNotAllowedException : ApplicationUnprocessableException(
    code = "$UPDATE_CONTROL_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_CONTROL_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.status.not.allowed")
)