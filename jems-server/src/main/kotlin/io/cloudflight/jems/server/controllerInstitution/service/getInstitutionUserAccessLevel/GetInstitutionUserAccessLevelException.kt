package io.cloudflight.jems.server.controllerInstitution.service.getInstitutionUserAccessLevel

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException


class GetInstitutionUserAccessLevelException(cause: Throwable) : ApplicationException(
    code = "S-GCUALFP",
    i18nMessage = I18nMessage("use.case.get.controller.user.access.level.for.partner.failed"),
    cause = cause
)
