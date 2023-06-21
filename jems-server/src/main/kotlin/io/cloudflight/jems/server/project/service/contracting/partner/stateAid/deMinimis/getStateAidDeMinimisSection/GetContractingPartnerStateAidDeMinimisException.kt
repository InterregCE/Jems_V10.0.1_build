package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.getStateAidDeMinimisSection

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val CONTRACTING_PARTNER_STATE_AID_GET_DE_MINIMIS_ERROR_CODE_PREFIX = "S-CPSAGDM"
private const val CONTRACTING_PARTNER_STATE_AID_GET_DE_MINIMIS_ERROR_KEY_PREFIX = "project.contracting.partner.get.minimis"


class GetContractingPartnerStateAidDeMinimisException (cause: Throwable): ApplicationException(
    code = CONTRACTING_PARTNER_STATE_AID_GET_DE_MINIMIS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CONTRACTING_PARTNER_STATE_AID_GET_DE_MINIMIS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
