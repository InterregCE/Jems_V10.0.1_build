package io.cloudflight.jems.server.project.service.checklist.create.closure

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val CREATE_CLOSURE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX = "S-C-CRCIC"
const val CREATE_CLOSURE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX = "use.case.create.closure.checklist.instance"

class CreateClosureChecklistInstanceException(cause: Throwable) : ApplicationException(
    code = CREATE_CLOSURE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CREATE_CLOSURE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.failed"), cause = cause
)

class CreateClosureChecklistInstanceStatusNotAllowedException : ApplicationUnprocessableException(
    code = "$CREATE_CLOSURE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$CREATE_CLOSURE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.status.not.allowed")
)

class CreateClosureChecklistInstanceNotFinalReportException : ApplicationUnprocessableException(
    code = "$CREATE_CLOSURE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$CREATE_CLOSURE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.report.not.final")
)

class CreateClosureChecklistInstanceTypeNotValidException : ApplicationUnprocessableException(
    code = "$CREATE_CLOSURE_CHECKLIST_INSTANCE_ERROR_CODE_PREFIX-004",
    i18nMessage = I18nMessage("$CREATE_CLOSURE_CHECKLIST_INSTANCE_ERROR_KEY_PREFIX.checklist.type.not.closure")
)
