package io.cloudflight.jems.server.project.service.checklist.create.contracting

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val CREATE_CONTRACTING_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX = "S-CTR-CHIC"
const val CREATE_CONTRACTING_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX = "use.case.create.contracting.checklist.instance"

class CreateContractingChecklistInstanceException(cause: Throwable) : ApplicationException(
    code = CREATE_CONTRACTING_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CREATE_CONTRACTING_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.failed"), cause = cause
)
