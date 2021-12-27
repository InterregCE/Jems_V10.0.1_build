package io.cloudflight.jems.server.project.service.partner.delete_project_partner

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val DELETE_PROJECT_PARTNER_ERROR_CODE_PREFIX = "S-DPP"
private const val DELETE_PROJECT_PARTNER_ERROR_KEY_PREFIX = "use.case.delete.project.partner"

class DeleteProjectPartnerException(cause: Throwable) : ApplicationException(
    code = DELETE_PROJECT_PARTNER_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_PROJECT_PARTNER_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class PartnerCannotBeDeletedException : ApplicationUnprocessableException(
    code = "$DELETE_PROJECT_PARTNER_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_PROJECT_PARTNER_ERROR_KEY_PREFIX.cannot.be.deleted.in.the.current.status.of.project")
)
