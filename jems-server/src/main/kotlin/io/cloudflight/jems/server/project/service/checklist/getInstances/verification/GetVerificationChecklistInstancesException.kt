package io.cloudflight.jems.server.project.service.checklist.getInstances.verification

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

const val GET_VERIFICATION_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX = "S-V-CHIN"
const val GET_VERIFICATION_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX = "use.case.get.verification.checklist.instance"

class GetVerificationChecklistInstanceException(cause: Throwable) : ApplicationException(
    code = GET_VERIFICATION_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_VERIFICATION_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.failed"), cause = cause
)

class GetVerificationChecklistInstanceDetailNotFoundException : ApplicationNotFoundException(
    code = "$GET_VERIFICATION_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$GET_VERIFICATION_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.detail.not.found")
)
