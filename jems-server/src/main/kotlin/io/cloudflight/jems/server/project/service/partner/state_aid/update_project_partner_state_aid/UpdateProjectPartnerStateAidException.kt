package io.cloudflight.jems.server.project.service.partner.state_aid.update_project_partner_state_aid

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val UPDATE_PROJECT_PARTNER_STATE_AID_ERROR_CODE_PREFIX = "S-UPPSA"
private const val UPDATE_PROJECT_PARTNER_STATE_AID_ERROR_KEY_PREFIX = "use.case.update.project.partner.state.aid"

class UpdateProjectPartnerStateAidException(cause: Throwable) : ApplicationException(
    code = UPDATE_PROJECT_PARTNER_STATE_AID_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PROJECT_PARTNER_STATE_AID_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
