package io.cloudflight.jems.server.project.service.partner.state_aid.get_project_partner_state_aid

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_PARTNER_STATE_AID_ERROR_CODE_PREFIX = "S-GPPSA"
private const val GET_PROJECT_PARTNER_STATE_AID_ERROR_KEY_PREFIX = "use.case.get.project.partner.state.aid"

class GetProjectPartnerStateAidException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_PARTNER_STATE_AID_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_PARTNER_STATE_AID_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
