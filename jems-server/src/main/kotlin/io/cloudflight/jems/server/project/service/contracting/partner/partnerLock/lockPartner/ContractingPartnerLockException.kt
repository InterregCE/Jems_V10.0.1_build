package io.cloudflight.jems.server.project.service.contracting.partner.partnerLock.lockPartner

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val CONTRACTING_PARTNER_LOCK_ERROR_CODE_PREFIX = "S-CPLK"
private const val CONTRACTING_PARTNER_LOCK_ERROR_KEY_PREFIX = "project.contracting.partner.lock"


class ContractingPartnerLockException (cause: Throwable): ApplicationException(
    code = CONTRACTING_PARTNER_LOCK_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CONTRACTING_PARTNER_LOCK_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

