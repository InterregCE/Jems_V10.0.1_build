package io.cloudflight.jems.server.project.service.checklist.create.verification

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val CREATE_VERIFICATION_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX = "S-V-CHIC"
const val CREATE_VERIFICATION_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX = "use.case.create.verification.checklist.instance"

class CreateVerificationChecklistInstanceException(cause: Throwable) : ApplicationException(
    code = CREATE_VERIFICATION_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CREATE_VERIFICATION_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.failed"), cause = cause
)

class CreateVerificationChecklistInstanceStatusNotAllowedException : ApplicationUnprocessableException(
    code = "$CREATE_VERIFICATION_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$CREATE_VERIFICATION_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.status.not.allowed")
)
