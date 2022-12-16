package io.cloudflight.jems.server.controllerInstitution.service.getInstitutionUsers

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

const val GET_INSTITUTION_USERS_ERROR_CODE_PREFIX = "S-GIU"
const val GET_INSTITUTION_USERS_ERROR_KEY_PREFIX = "use.case.get.institution.users"

class GetInstitutionUsersException(cause: Throwable) : ApplicationException(
    code = "$GET_INSTITUTION_USERS_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$GET_INSTITUTION_USERS_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
