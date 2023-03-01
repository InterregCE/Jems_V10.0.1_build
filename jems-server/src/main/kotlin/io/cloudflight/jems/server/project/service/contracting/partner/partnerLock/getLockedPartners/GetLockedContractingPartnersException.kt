package io.cloudflight.jems.server.project.service.contracting.partner.partnerLock.getLockedPartners

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_LOCKED_CONTRACTING_PARTNERS_ERROR_CODE_PREFIX = "S-GLKCP"
private const val GET_LOCKED_CONTRACTING_PARTNERS_ERROR_KEY_PREFIX = "project.contracting.partners.locked"

class GetLockedContractingPartnersException (cause: Throwable): ApplicationException(
    code = GET_LOCKED_CONTRACTING_PARTNERS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_LOCKED_CONTRACTING_PARTNERS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
