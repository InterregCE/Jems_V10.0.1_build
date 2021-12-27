package io.cloudflight.jems.server.project.service.partner.deactivate_project_partner

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val DEACTIVATE_PROJECT_PARTNER_ERROR_CODE_PREFIX = "S-DEPP"
private const val DEACTIVATE_PROJECT_PARTNER_ERROR_KEY_PREFIX = "use.case.deactivate.project.partner"

class DeactivateProjectPartnerException(cause: Throwable) : ApplicationException(
    code = DEACTIVATE_PROJECT_PARTNER_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DEACTIVATE_PROJECT_PARTNER_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
class PartnerCannotBeDeactivatedException : ApplicationUnprocessableException(
    code = "$DEACTIVATE_PROJECT_PARTNER_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DEACTIVATE_PROJECT_PARTNER_ERROR_KEY_PREFIX.cannot.be.deactivated.in.the.current.status.of.project")
)
