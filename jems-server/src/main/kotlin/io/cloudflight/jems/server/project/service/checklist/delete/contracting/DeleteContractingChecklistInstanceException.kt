package io.cloudflight.jems.server.project.service.checklist.delete.contracting

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val DELETE_CONTRACTING_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX = "S-CTR-CHID"
const val DELETE_CONTRACTING_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX = "use.case.delete.contracting.checklist.instance"

class DeleteContractingChecklistInstanceException(cause: Throwable) : ApplicationException(
    code = DELETE_CONTRACTING_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_CONTRACTING_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.failed"), cause = cause
)

class DeleteContractingChecklistInstanceStatusNotAllowedException : ApplicationUnprocessableException(
    code = "$DELETE_CONTRACTING_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_CONTRACTING_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.status.not.allowed")
)
