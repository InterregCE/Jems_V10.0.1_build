package io.cloudflight.jems.server.programme.service.checklist.create

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val CREATE_PROGRAMME_CHECKLIST_ERROR_CODE_PREFIX = "S-PCHC"
const val CREATE_PROGRAMME_CHECKLIST_ERROR_KEY_PREFIX = "use.case.create.programme.checklist"

class CreateProgrammeChecklistException(cause: Throwable) : ApplicationException(
    code = "$CREATE_PROGRAMME_CHECKLIST_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CREATE_PROGRAMME_CHECKLIST_ERROR_KEY_PREFIX.failed"), cause = cause
)

class MaxAmountOfProgrammeChecklistReached(maxAmount: Int) : ApplicationUnprocessableException(
    code = "$CREATE_PROGRAMME_CHECKLIST_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage(
        i18nKey = "$CREATE_PROGRAMME_CHECKLIST_ERROR_CODE_PREFIX.max.amount.reached",
        i18nArguments = mapOf(
            "maxSize" to maxAmount.toString()
        )
    )
)
