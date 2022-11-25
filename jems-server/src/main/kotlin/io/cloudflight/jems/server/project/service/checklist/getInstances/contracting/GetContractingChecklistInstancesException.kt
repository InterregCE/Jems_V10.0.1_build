package io.cloudflight.jems.server.project.service.checklist.getInstances.contracting

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

const val GET_CONTRACTING_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX = "S-CTR-CHIN"
const val GET_CONTRACTING_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX = "use.case.get.contracting.checklist.instance"

class GetContractingChecklistInstanceException(cause: Throwable): ApplicationException(
   code = GET_CONTRACTING_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX,
   i18nMessage = I18nMessage("$GET_CONTRACTING_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.failed"), cause = cause
)

class GetContractingChecklistInstanceDetailNotFoundException: ApplicationNotFoundException(
    code = "$GET_CONTRACTING_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$GET_CONTRACTING_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.detail.not.found")
)
