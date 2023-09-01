package io.cloudflight.jems.server.project.service.checklist.update.verification

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val UPDATE_VERIFICATION_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX = "S-V-PCHC"
const val UPDATE_VERIFICATION_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX = "use.case.update.verification.checklist.instance"

class UpdateVerificationChecklistInstanceException(cause: Throwable) : ApplicationException(
    code = UPDATE_VERIFICATION_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_VERIFICATION_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.failed"), cause = cause
)

class UpdateVerificationChecklistInstanceStatusException(cause: Throwable) : ApplicationException(
    code = "$UPDATE_VERIFICATION_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_VERIFICATION_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.status.failed"), cause = cause
)

class UpdateVerificationChecklistInstanceStatusNotAllowedException : ApplicationUnprocessableException(
    code = "$UPDATE_VERIFICATION_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_VERIFICATION_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.status.not.allowed")
)

class UpdateVerificationChecklistInstanceNotFoundException : ApplicationNotFoundException(
    code = "$UPDATE_VERIFICATION_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$UPDATE_VERIFICATION_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.not.found")
)

