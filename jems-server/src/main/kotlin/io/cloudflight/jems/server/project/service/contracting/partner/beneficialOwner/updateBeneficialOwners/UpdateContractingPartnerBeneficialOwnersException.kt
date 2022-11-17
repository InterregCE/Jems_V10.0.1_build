package io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.updateBeneficialOwners

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_PROJECT_CONTRACTING_PARTNER_BENEFICIAL_OWNER_ERROR_CODE_PREFIX = "S-UPCPBO"
private const val UPDATE_PROJECT_CONTRACTING_PARTNER_BENEFICIAL_OWNER_ERROR_KEY_PREFIX = "use.case.update.project.contracting.partner.beneficial.owner"

class UpdateContractingPartnerBeneficialOwnersException(cause: Throwable) : ApplicationException(
    code = UPDATE_PROJECT_CONTRACTING_PARTNER_BENEFICIAL_OWNER_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PROJECT_CONTRACTING_PARTNER_BENEFICIAL_OWNER_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class MaxAmountOfBeneficialOwnersReachedException(maxAmount: Int) : ApplicationUnprocessableException(
    code = "$UPDATE_PROJECT_CONTRACTING_PARTNER_BENEFICIAL_OWNER_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage(i18nKey = "$UPDATE_PROJECT_CONTRACTING_PARTNER_BENEFICIAL_OWNER_ERROR_KEY_PREFIX.max.amount.of.beneficials.reached"),
    message = "max allowed: $maxAmount",
)
