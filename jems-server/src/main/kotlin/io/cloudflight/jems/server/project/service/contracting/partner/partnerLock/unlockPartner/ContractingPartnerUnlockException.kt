package io.cloudflight.jems.server.project.service.contracting.partner.partnerLock.unlockPartner

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val CONTRACTING_PARTNER_UNLOCK_ERROR_CODE_PREFIX = "S-CPULK"
private const val CONTRACTING_PARTNER_UNLOCK_ERROR_KEY_PREFIX = "project.contracting.partner.unlock"


class ContractingPartnerUnlockException (cause: Throwable): ApplicationException(
    code = CONTRACTING_PARTNER_UNLOCK_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CONTRACTING_PARTNER_UNLOCK_ERROR_KEY_PREFIX.failed"),
    cause = cause
)