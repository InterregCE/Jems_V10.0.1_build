package io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.getBeneficialOwners

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_CONTRACTING_PARTNER_BENEFICIAL_OWNER_ERROR_CODE_PREFIX = "S-GPCPBO"
private const val GET_PROJECT_CONTRACTING_PARTNER_BENEFICIAL_OWNER_ERROR_KEY_PREFIX = "use.case.get.project.contracting.partner.beneficial.owner"

class GetContractingPartnerBeneficialOwnersException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_CONTRACTING_PARTNER_BENEFICIAL_OWNER_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_CONTRACTING_PARTNER_BENEFICIAL_OWNER_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
