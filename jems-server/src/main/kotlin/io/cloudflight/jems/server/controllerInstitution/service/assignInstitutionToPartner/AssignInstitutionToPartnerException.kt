package io.cloudflight.jems.server.controllerInstitution.service.assignInstitutionToPartner

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

const val ASSIGN_INSTITUTION_TO_PARTNER_ERROR_CODE_PREFIX = "S-AITP"
const val ASSIGN_INSTITUTION_TO_PARTNER_ERROR_KEY_PREFIX = "use.case.assign.controller.institution.to.partner"


class AssignInstitutionToPartnerException(cause: Throwable) : ApplicationException(
    code = "$ASSIGN_INSTITUTION_TO_PARTNER_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$ASSIGN_INSTITUTION_TO_PARTNER_ERROR_KEY_PREFIX.failed"),
    cause = cause
)


class ProjectPartnerNotValidException: ApplicationNotFoundException(
    code = "$ASSIGN_INSTITUTION_TO_PARTNER_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$ASSIGN_INSTITUTION_TO_PARTNER_ERROR_KEY_PREFIX.failed"),
)

