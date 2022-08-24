package io.cloudflight.jems.server.controllerInstitution.service.getInstitutionPartnerAssignment

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException


const val GET_INSTITUTION_PARTNER_ASSIGNMENT_ERROR_CODE_PREFIX = "S-AITP"
const val GET_INSTITUTION_PARTNER_ASSIGNMENT_ERROR_KEY_PREFIX = "use.case.get.institution.to.partner.assignments"


class GetInstitutionPartnerAssignmentException(cause: Throwable) : ApplicationException(
    code = "$GET_INSTITUTION_PARTNER_ASSIGNMENT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$GET_INSTITUTION_PARTNER_ASSIGNMENT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class GetControllerUserAccessLevelForPartnerException(cause: Throwable) : ApplicationException(
    code = "S-GCUALFP",
    i18nMessage = I18nMessage("use.case.get.controller.user.access.level.for.partner.failed"),
    cause = cause
)
