package io.cloudflight.jems.server.project.service.checklist.export

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.project.service.checklist.getInstances.GET_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX
import io.cloudflight.jems.server.project.service.checklist.getInstances.GET_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX

const val EXPORT_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX = "S-ECHI"
const val EXPORT_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX = "use.case.export.checklist.instance"

class ExportChecklistInstanceException(cause: Throwable): ApplicationException(
    code = "$EXPORT_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$EXPORT_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.failed"), cause = cause
)

class ExportChecklistInstanceNotFoundException : ApplicationNotFoundException(
    code = "$EXPORT_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$EXPORT_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.detail.not.found"),
)
