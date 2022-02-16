package io.cloudflight.jems.server.project.service.partnerUser.getMyPartnerCollaboratorLevel

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_MY_PARTNER_COLLABORATOR_LEVEL_ERROR_CODE_PREFIX = "S-GMPCL"
private const val GET_MY_PARTNER_COLLABORATOR_LEVEL_ERROR_KEY_PREFIX = "use.case.get.my.partner.collaborator.level"

class GetMyPartnerCollaboratorLevelException(cause: Throwable) : ApplicationException(
    code = GET_MY_PARTNER_COLLABORATOR_LEVEL_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_MY_PARTNER_COLLABORATOR_LEVEL_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
