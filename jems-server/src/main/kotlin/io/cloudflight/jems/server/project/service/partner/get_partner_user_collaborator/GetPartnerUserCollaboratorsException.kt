package io.cloudflight.jems.server.project.service.partner.get_partner_user_collaborator

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PARTNER_USER_COLLABORATORS_ERROR_CODE_PREFIX = "S-GPUC"
private const val GET_PARTNER_USER_COLLABORATORS_ERROR_KEY_PREFIX = "use.case.get.partner.user.collaborators"

class GetPartnerUserCollaboratorsException(cause: Throwable) : ApplicationException(
    code = GET_PARTNER_USER_COLLABORATORS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PARTNER_USER_COLLABORATORS_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
