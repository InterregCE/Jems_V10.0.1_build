package io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.updateBankingDetails

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_PROJECT_CONTRACTING_PARTNER_BANKING_DETAILS_ERROR_CODE_PREFIX = "S-GPCPBO"
private const val UPDATE_PROJECT_CONTRACTING_PARTNER_BANKING_DETAILS_ERROR_KEY_PREFIX = "use.case.update.project.contracting.partner.banking.details"

class UpdateContractingPartnerBankingDetailsException(cause: Throwable): ApplicationException(
    code = UPDATE_PROJECT_CONTRACTING_PARTNER_BANKING_DETAILS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("${UPDATE_PROJECT_CONTRACTING_PARTNER_BANKING_DETAILS_ERROR_KEY_PREFIX}.failed"),
    cause = cause
)

class UpdateContractingPartnerBankingDetailsNotAllowedException: ApplicationUnprocessableException(
    code ="$UPDATE_PROJECT_CONTRACTING_PARTNER_BANKING_DETAILS_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_PROJECT_CONTRACTING_PARTNER_BANKING_DETAILS_ERROR_KEY_PREFIX.is.not.allowed")
)