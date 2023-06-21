package io.cloudflight.jems.server.project.service.checklist.update.contracting

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException
import io.cloudflight.jems.server.project.service.checklist.update.UPDATE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX
import io.cloudflight.jems.server.project.service.checklist.update.UPDATE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX

const val UPDATE_CONTRACTING_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX = "S-UP-CTR-CHIN"
const val UPDATE_CONTRACTING_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX = "use.case.update.contracting.checklist.instance"

class UpdateContractingChecklistInstanceException(cause: Throwable) : ApplicationException(
    code = UPDATE_CONTRACTING_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_CONTRACTING_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.failed"), cause = cause
)

class UpdateContractingChecklistInstanceStatusException(cause: Throwable) : ApplicationException(
    code = "$UPDATE_CONTRACTING_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_CONTRACTING_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.status.failed"), cause = cause
)

class UpdateContractingChecklistInstanceStatusNotAllowedException : ApplicationUnprocessableException(
    code = "$UPDATE_CONTRACTING_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_CONTRACTING_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.status.not.allowed")
)

class UpdateContractingChecklistInstanceNotFoundException : ApplicationNotFoundException(
    code = "$UPDATE_CONTRACTING_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$UPDATE_CONTRACTING_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.not.found")
)

