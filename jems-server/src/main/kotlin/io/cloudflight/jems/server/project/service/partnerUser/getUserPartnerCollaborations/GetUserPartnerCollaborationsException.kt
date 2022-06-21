package io.cloudflight.jems.server.project.service.partnerUser.getUserPartnerCollaborations

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_USER_PARTNER_COLLABORATIONS_ERROR_CODE_PREFIX = "S-GUPC"
private const val GET_USER_PARTNER_COLLABORATIONS_ERROR_KEY_PREFIX = "use.case.get.user.partner.collaborations"


class GetUserPartnerCollaborationsException(cause: Throwable) : ApplicationException(
    code = GET_USER_PARTNER_COLLABORATIONS_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_USER_PARTNER_COLLABORATIONS_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
