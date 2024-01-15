package io.cloudflight.jems.server.programme.service.checklist.clone

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val CLONE_PROGRAMME_CHECKLIST_ERROR_CODE_PREFIX = "S-PCHC"
const val CLONE_PROGRAMME_CHECKLIST_ERROR_KEY_PREFIX = "use.case.clone.programme.checklist"

class CloneProgrammeChecklistException(cause: Throwable) : ApplicationException(
    code = "$CLONE_PROGRAMME_CHECKLIST_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CLONE_PROGRAMME_CHECKLIST_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class MaxAmountOfProgrammeChecklistReached(maxAmount: Int) : ApplicationUnprocessableException(
    code = "$CLONE_PROGRAMME_CHECKLIST_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage(
        i18nKey = "$CLONE_PROGRAMME_CHECKLIST_ERROR_CODE_PREFIX.max.amount.reached",
        i18nArguments = mapOf(
            "maxSize" to maxAmount.toString()
        )
    )
)
