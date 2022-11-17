package io.cloudflight.jems.server.controllerInstitution.service.checkInstitutionPartnerAssignment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException


class CheckInstitutionPartnerAssignmentsException(cause: Throwable) : ApplicationException(
    code = "S-CIUPA",
    i18nMessage = I18nMessage("use.case.check.controller.institution.partner.assignment.failed"),
    cause = cause
)
