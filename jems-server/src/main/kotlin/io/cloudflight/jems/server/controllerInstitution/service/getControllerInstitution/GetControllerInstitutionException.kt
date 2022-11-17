package io.cloudflight.jems.server.controllerInstitution.service.getControllerInstitution

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

const val GET_CONTROLLER_INSTITUTION_ERROR_CODE_PREFIX = "S-CIN"
const val GET_CONTROLLER_INSTITUTION_ERROR_KEY_PREFIX = "use.case.get.controller.institution"

class GetControllerInstitutionException : ApplicationNotFoundException(
    code = "$GET_CONTROLLER_INSTITUTION_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$GET_CONTROLLER_INSTITUTION_ERROR_KEY_PREFIX.failed")
)

