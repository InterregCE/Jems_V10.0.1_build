package io.cloudflight.jems.server.controllerInstitution.service.updateInstitutionUsersProjectAssignment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

class UpdateInstitutionUserProjectAssignmentException(cause: Throwable) : ApplicationException(
    code = "S-UCIPA",
    i18nMessage = I18nMessage("use.case.update.controller.institution.user.project.assignment.failed"),
    cause = cause
)
