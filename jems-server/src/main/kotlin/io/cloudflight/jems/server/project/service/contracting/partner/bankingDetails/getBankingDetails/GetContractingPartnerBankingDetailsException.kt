package io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.getBankingDetails

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val GET_PROJECT_CONTRACTING_PARTNER_BANKING_DETAILS_ERROR_CODE_PREFIX = "S-GPCPBO"
private const val GET_PROJECT_CONTRACTING_PARTNER_BANKING_DETAILS_ERROR_KEY_PREFIX = "use.case.get.project.contracting.partner.banking.details"

class GetContractingPartnerBankingDetailsException(cause: Throwable): ApplicationException(
    code = GET_PROJECT_CONTRACTING_PARTNER_BANKING_DETAILS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_CONTRACTING_PARTNER_BANKING_DETAILS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class GetContractingPartnerBankingDetailsPartnerNotFoundException : ApplicationUnprocessableException(
    code = "$GET_PROJECT_CONTRACTING_PARTNER_BANKING_DETAILS_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$GET_PROJECT_CONTRACTING_PARTNER_BANKING_DETAILS_ERROR_KEY_PREFIX.partner.not.found")
)

class GetContractingPartnerBankingDetailsNotAllowedException : ApplicationUnprocessableException(
    code = "$GET_PROJECT_CONTRACTING_PARTNER_BANKING_DETAILS_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$GET_PROJECT_CONTRACTING_PARTNER_BANKING_DETAILS_ERROR_KEY_PREFIX.not.allowed")
)