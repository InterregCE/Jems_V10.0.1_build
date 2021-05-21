package io.cloudflight.jems.server.project.service.partner.get_project_partner

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_PARTNER_ERROR_CODE_PREFIX = "S-GPP"
private const val GET_PROJECT_PARTNER_ERROR_KEY_PREFIX = "use.case.get.project.partner"

class GetProjectPartnerByIdException(cause: Throwable) : ApplicationException(
    code = "$GET_PROJECT_PARTNER_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$GET_PROJECT_PARTNER_ERROR_KEY_PREFIX.by.id.failed"),
    cause = cause
)

class GetProjectPartnersByProjectIdException(cause: Throwable) : ApplicationException(
    code = "$GET_PROJECT_PARTNER_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$GET_PROJECT_PARTNER_ERROR_KEY_PREFIX.by.project.id.failed"),
    cause = cause
)

class GetProjectPartnerByProjectIdForDropdownException(cause: Throwable) : ApplicationException(
    code = "$GET_PROJECT_PARTNER_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$GET_PROJECT_PARTNER_ERROR_KEY_PREFIX.by.project.id.for.dropdown.failed"),
    cause = cause
)