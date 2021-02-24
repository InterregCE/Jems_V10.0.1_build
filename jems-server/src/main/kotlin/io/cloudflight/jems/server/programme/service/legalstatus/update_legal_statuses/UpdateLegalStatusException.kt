package io.cloudflight.jems.server.programme.service.legalstatus.update_legal_statuses

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationBadRequestException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

const val UPDATE_LEGAL_STATUSES_ERROR_CODE_PREFIX = "S-ULS"
const val UPDATE_LEGAL_STATUSES_ERROR_KEY_PREFIX = "use.case.update.legal.status"

class DeletionWhenProgrammeSetupRestricted : ApplicationBadRequestException(
    code = "$UPDATE_LEGAL_STATUSES_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_LEGAL_STATUSES_ERROR_KEY_PREFIX.programme.setup.restricted"),
)

class MaxAllowedLegalStatusesReachedException(amount: Long) :
    ApplicationBadRequestException(
        code = "$UPDATE_LEGAL_STATUSES_ERROR_CODE_PREFIX-002",
        i18nMessage = I18nMessage("$UPDATE_LEGAL_STATUSES_ERROR_KEY_PREFIX.max.allowed.amount.reached"),
        message = "max allowed: $amount",
    )

class LegalStatusesDescriptionTooLong() :
    ApplicationUnprocessableException(
        code = "$UPDATE_LEGAL_STATUSES_ERROR_CODE_PREFIX-003",
        i18nMessage = I18nMessage("$UPDATE_LEGAL_STATUSES_ERROR_KEY_PREFIX.description.too.long"),
    )
