package io.cloudflight.jems.server.programme.service.checklist.delete

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException
import io.cloudflight.jems.server.programme.service.checklist.update.UPDATE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX
import io.cloudflight.jems.server.programme.service.checklist.update.UPDATE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX

const val DELETE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX = "S-CHID"
const val DELETE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX = "use.case.delete.checklist.instance"

class DeleteChecklistInstanceException(cause: Throwable) : ApplicationException(
    code = "$DELETE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.failed"), cause = cause
)

class DeleteChecklistInstanceStatusNotAllowedException : ApplicationUnprocessableException(
    code = "$UPDATE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.status.not.allowed")
)
